import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SeleniumTest {

    //Simple Selenium Test

    static final String Path_To_geckodriver_exe ="C:\\\\Users\\Administrator\\Desktop\\" +
            "geckodriver-v0.21.0-win64\\geckodriver.exe";

    public static void main(String[] args){
        System.setProperty("webdriver.gecko.driver", Path_To_geckodriver_exe);

        WebDriver driver = new FirefoxDriver();
        driver.get("http://www.google.com");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.quit();
        System.out.println("Heppy end");
    }
}
