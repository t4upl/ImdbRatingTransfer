import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ImdbTransfer {

    //NOTE!:
    //Firefox: 60.0.2 (64-bit)
    //Selenium 3.14.0
    //Geckodriver v0.21.0-win64
    //May not work with different versions


    //USER PROPERTIES
    private static final String PATH_TO_OLD_ACCOUNT_RATINGS = "";
    private static final String PATH_TO_GECKODRIVER_EXE = "";
    private static final String MY_EMAIL = "";
    private static final String MY_EMAIL_PASSWORD = "";

    //OTHER PROPERTIES
    private static final int IMPLICT_WAIT_SELENIUM_IN_SECONDS = 12;

    //STATIC TEXTS SELECTORS
    private static final String OTHER_SIGN_IN_OPTIONS_TEXT = "Other Sign in options";
    private static final String SIGN_IN_WITH_GOOGLE_TEXT = "Sign in with Google";
    private static final String NEXT_TEXT = "Next";

    //Local variables
    static WebDriver driver;

    public static void main(String[] args){
        checkUserProperties();

        System.setProperty("webdriver.gecko.driver", PATH_TO_GECKODRIVER_EXE);

        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(IMPLICT_WAIT_SELENIUM_IN_SECONDS, TimeUnit.SECONDS);
        driver.get(PATH_TO_OLD_ACCOUNT_RATINGS);

        findAndClickWebElementByXpath(OTHER_SIGN_IN_OPTIONS_TEXT);
        findAndClickWebElementByXpath(SIGN_IN_WITH_GOOGLE_TEXT);

        //Signing to google account
        driver.findElement(By.cssSelector("input[type=email]")).sendKeys(MY_EMAIL);
        findAndClickWebElementByXpath(NEXT_TEXT, "span");

        new WebDriverWait(driver, 20).until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("input[type=password]")));
        driver.findElement(By.cssSelector("input[type=password]")).sendKeys(MY_EMAIL_PASSWORD);
        findAndClickWebElementByXpath(NEXT_TEXT, "span");

        //IMDB other users rating page
        String nextButtonXpath = "//*[contains(text(), '" + NEXT_TEXT + "') and contains(@href,'user')]";
        while (true) {
            List<WebElement> movieList = driver.findElements(By.xpath(
                    "//div[@class = 'lister-item-content']//a[contains(@href,'/title') " +
                            "and not (contains(@href,'/plot')) ]"));

            //update current user ratings based on movie titles
            movieList.forEach(x -> updateMovieRating(x.getText()));

            List<WebElement> nextButtonList = driver.findElements(By.xpath(nextButtonXpath));
            if (nextButtonList.isEmpty()) {
                break;
            }

            //click "Next" button to go to next page
            WebElement nextButton = driver.findElement(By.xpath(nextButtonXpath));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nextButton);
            nextButton.click();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.quit();
        System.out.println("Happy end!");
    }

    private static void checkUserProperties() {
        List<String> list = new ArrayList<>(Arrays.asList(PATH_TO_OLD_ACCOUNT_RATINGS, PATH_TO_GECKODRIVER_EXE,
                MY_EMAIL, MY_EMAIL_PASSWORD));

        Boolean isAnyPropertyEmpty = list
                .stream()
                .reduce(false,
                        (result, string) -> result || string.isEmpty(),
                        (result1, result2) -> { return result1 || result2;});

        if (isAnyPropertyEmpty) {
            throw new RuntimeException("One of user properties is empty, Please set it.");
        }
    }

    private static void updateMovieRating(String movieTitle) {
        String otherUserMovieRating = driver.findElement(
                By.xpath("//div[@class = 'lister-item-content']" +
                            "//a[contains(@href,'/title') " +
                            "and not (contains(@href,'/plot')) and contains(text(), \"" + movieTitle + "\" )]" +
                            "/ancestor::div[@class='lister-item-content']" +
                            "//*[contains (@class, 'ipl-rating-star--other-user small')]" +
                            "//span[@class='ipl-rating-star__rating']")).getText();

        WebElement ratingStar = driver.findElement(By.xpath(
                "//div[@class = 'lister-item-content']" +
                "//a[contains(@href,'/title') " +
                "and not (contains(@href,'/plot')) and contains(text(), \"" + movieTitle + "\" )]" +
                "/ancestor::div[@class='lister-item-content']" +
                "//*[contains (@class, 'ipl-rating-interactive')]//input[@type = 'checkbox']"));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", ratingStar);
        ratingStar.click();

        //rate movie on the current user account to the same value as other user
        driver.findElement(By.xpath(
                "//div[@class = 'lister-item-content']" +
                        "//a[contains(@href,'/title') " +
                        "and not (contains(@href,'/plot')) and contains(text(), \"" + movieTitle + "\" )]" +
                        "/ancestor::div[@class='lister-item-content']" +
                        "//*[contains (@class, 'ipl-rating-interactive')]" +
                        "//form//*[@data-value='" + otherUserMovieRating + "']")).click();
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void findAndClickWebElementByXpath(String textIdentifiesWebElement, String htmlElement) {
        String xpath = "[contains(text(), '" + textIdentifiesWebElement + "')]";
        if (htmlElement == null) {
            xpath = "//*" + xpath;
        } else {
            xpath = "//" + htmlElement + xpath;
        }

        List<WebElement> list = driver.findElements(By.xpath(xpath));
        if (list.size() != 1) {
            System.out.println("Problem in findAndClickWebElementByXpath: " + list.size() + " " +
                    textIdentifiesWebElement + "\n "+ xpath);
        }

        driver.findElement(By.xpath(xpath)).click();
    }

    private static void findAndClickWebElementByXpath(String textIdentifiesWebElement) {
        findAndClickWebElementByXpath(textIdentifiesWebElement, null);
    }
}
