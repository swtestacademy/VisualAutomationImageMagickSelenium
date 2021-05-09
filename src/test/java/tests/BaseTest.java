package tests;

import java.lang.reflect.Method;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import steps.Steps;
import utils.ChromeOptionsUtil;

public class BaseTest {
    ChromeOptionsUtil chromeOptionsUtil = new ChromeOptionsUtil();
    public WebDriver driver;
    Steps steps;

    @BeforeClass
    public void setupTestClass() {
        driver = new ChromeDriver(chromeOptionsUtil.getChromeOptions());
        driver.manage().window().maximize();
        steps = new Steps(driver);
        steps.preTestSetup();
    }

    @BeforeMethod
    public void setupTestMethod(Method method) {
        steps.folderUtil.setUpFilesAndFolders(method.getName());
    }

    @AfterClass
    public void quitDriver() {
        driver.quit();
    }
}
