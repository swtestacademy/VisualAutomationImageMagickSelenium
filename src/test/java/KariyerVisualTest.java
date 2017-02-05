import org.apache.commons.io.FileUtils;
import org.im4java.core.CompareCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessStarter;
import org.im4java.process.StandardStream;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import com.google.common.io.Files;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by onurb on 28-Aug-16.
 */
public class KariyerVisualTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;


    //URL of the test website
    private String url = "http://www.kariyer.net";

    //Main Directory of the test code
    private String currentDir = System.getProperty("user.dir");

    //Main screenshot directory
    private String parentScreenShotsLocation = currentDir + "\\ScreenShots\\";

    //Main differences directory
    private String parentDifferencesLocation = currentDir + "\\Differences\\";

    //Test name
    private String testName;

    //Test Screenshot directory
    private String testScreenShotDirectory;

    //Element screenshot paths
    private String baselineScreenShotPath;
    private String actualScreenShotPath;
    private String differenceScreenShotPath;

    //Image files
    public File baselineImageFile;
    public File actualImageFile;
    public File differenceImageFile;
    public File differenceFileForParent;

    //Setup Driver
    @BeforeClass
    public void setupTestClass() {
        //Declare Firefox driver
        driver = new ChromeDriver();

        //Go to URL
        driver.navigate().to(url);

        //Maximize the browser
        driver.manage().window().maximize();

        //Declare a 10 seconds wait time
        wait = new WebDriverWait(driver,10);

        //JS Executor
        js = (JavascriptExecutor) driver;

        //Create screenshot and differences folders
        createFolder(parentScreenShotsLocation);
        createFolder(parentDifferencesLocation);

        //Clean Differences Root Folder
        File differencesFolder = new File(parentDifferencesLocation);
        try {
            FileUtils.cleanDirectory(differencesFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Add Cookie for top banner
        addCookieforTopBanner();
    }

    @BeforeMethod
    public void setupTestMethod(Method method) {
        //Get the test name to create a specific screenshot folder for each test.
        testName = method.getName();
        System.out.println("Test Name: " + testName + "\n");

        //Create a specific directory for a test
        testScreenShotDirectory = parentScreenShotsLocation + testName + "\\";
        createFolder(testScreenShotDirectory);

        wait = new WebDriverWait(driver,10);

    }

    //Add Cookie not to see top banner animation
    private void addCookieforTopBanner () {
        //Get Next Month Last Date for cookie expiration
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date nextMonthLastDay = calendar.getTime();

        //Create/Build a cookie
        Cookie topBannerCloseCookie = new Cookie.Builder("AA-kobiBannerClosed","4") //Name & value pair of the cookie
                .domain("www.kariyer.net") //Domain of the cookie
                .path("/") //Path of the cookie
                .expiresOn(nextMonthLastDay) //Expiration date
                .build(); //Finally build it with .build() call

        //Add a cookie
        driver.manage().addCookie(topBannerCloseCookie);
    }

    //Create Folder Method
    private void createFolder (String path) {
        File testDirectory = new File(path);
        if (!testDirectory.exists()) {
            if (testDirectory.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        } else {
            System.out.println("Directory already exists: " + path);
        }
    }

    //Close popup if exists
    private void handlePopup(String selector) {
        List<WebElement> popup = driver.findElements(By.cssSelector(selector));
        if(!popup.isEmpty()){
            popup.get(0).click();
        }
    }

    //Take Screenshot with AShot
    private Screenshot takeScreenshot (WebElement element) {
        //Take screenshot with Ashot
        //AShot JQuery screenshot capture is not working. Thus, I used webdriver's CoordsProvider method.
        Screenshot elementScreenShot = new AShot().takeScreenshot(driver, element);
/*        Screenshot elementScreenShot = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .takeScreenshot(driver,element);*/

        //Print element size
        String size = "Height: " + elementScreenShot.getImage().getHeight() + "\n" +
                      "Width: " + elementScreenShot.getImage().getWidth() + "\n";
        System.out.print("Size: " + size);

        return elementScreenShot;
    }

    //Screenshot paths
    private void declareScreenShotPaths (String baseline, String actual, String diff) {
        //BaseLine, Current, Difference Photo Paths
        baselineScreenShotPath = testScreenShotDirectory + baseline;
        actualScreenShotPath = testScreenShotDirectory + actual;
        differenceScreenShotPath = testScreenShotDirectory + diff;


        //BaseLine, Current Photo Files
        baselineImageFile = new File(baselineScreenShotPath);
        actualImageFile = new File(actualScreenShotPath);
        differenceImageFile = new File (differenceScreenShotPath);

        //For copying difference to the parent Difference Folder
        differenceFileForParent = new File (parentDifferencesLocation + diff);
    }

    //ImageMagick Compare Method
    private boolean compareImagesWithImageMagick (String exp, String cur, String diff) throws Exception {
        // This class implements the processing of os-commands using a ProcessBuilder.
        // This is the core class of the im4java-library where all the magic takes place.
        // It does add some overhead compared to a direct call of ProcessBuilder,
        // but you gain additional features like piping and asynchronous execution.
        ProcessStarter.setGlobalSearchPath("C:\\Program Files\\ImageMagick-7.0.4-Q16");

        // This instance wraps the compare command
        CompareCmd compare = new CompareCmd();

        // Set the ErrorConsumer for the stderr of the ProcessStarter.
        compare.setErrorConsumer(StandardStream.STDERR);

        // Create ImageMagick Operation Object
        IMOperation cmpOp = new IMOperation();

        //Add option -fuzz to the ImageMagick commandline
        cmpOp.fuzz(10.0);
        cmpOp.metric("AE");

        // Add the expected image
        cmpOp.addImage(exp);

        // Add the current image
        cmpOp.addImage(cur);

        // This stores the difference
        cmpOp.addImage(diff);

        try {
            //Do the compare
            System.out.println ("Comparison Started!");
            compare.run(cmpOp);
            System.out.println ("Comparison Finished!");
            //DisplayCmd.show(diff);
            return true;
        }
        catch (Exception ex) {
            System.out.print(ex);
            System.out.println ("Comparison Failed!");
            //Put the difference also difference folder
            Files.copy(differenceImageFile,differenceFileForParent);
            throw ex;
        }
    }

    //Compare Operation
    private void doComparison (Screenshot elementScreenShot) throws Exception {
        //Did we capture baseline image before?
        if (baselineImageFile.exists()){
            //Compare screenshot with baseline
            System.out.println("Comparison method will be called!\n");

            System.out.println("Baseline: " + baselineScreenShotPath + "\n" +
                    "Actual: " + actualScreenShotPath + "\n" +
                    "Diff: " + differenceScreenShotPath);

            //Try to use IM API for comparison
            boolean compareResult = compareImagesWithImageMagick(baselineScreenShotPath, actualScreenShotPath, differenceScreenShotPath);

            //If comparison result true print that it passed.
            if(compareResult==true) {
                System.out.println ("Comparison Passed!");
            }

        } else {
            System.out.println("BaselineScreenshot is not exist! We put it into test screenshot folder.\n");
            //Put the screenshot to the specified folder
            ImageIO.write(elementScreenShot.getImage(), "PNG", baselineImageFile);
        }
    }

    @Test
    public void kariyerUzmanCssTest () throws Exception {
        //Wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").toString().equals("complete");
        wait.until(jsLoad);

        //JQuery Wait
        ExpectedCondition<Boolean> jQueryLoad = driver -> ((Long) ((JavascriptExecutor) driver)
                .executeScript("return jQuery.active") == 0);

        wait.until(jsLoad);
        wait.until(jQueryLoad);

        //Handle popup
        handlePopup(".ui-dialog-titlebar-close");

        wait.until(jsLoad);
        wait.until(jQueryLoad);
        Thread.sleep(200);

        //Close Banner
        try {
            List<WebElement> banner = driver.findElements(By.cssSelector("body > div.kobi-head-banner > div > a"));
            if(!banner.isEmpty()) {
                banner.get(0).click();
                //Wait for 2 second for closing banner
                wait.until(jsLoad);
                wait.until(jQueryLoad);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            System.out.println("Banner Closed Before!");
        }

        //Declare UZMAN photo section
        WebElement uzmanPhotoSection = driver.findElement(By.cssSelector(".item.uzman>a"));

        //Unhide Text (It is Changing A lot)
        js.executeScript("document.getElementsByClassName('count')[0].style.display='none';");
        wait.until(jsLoad);
        wait.until(jQueryLoad);
        Thread.sleep(200);

        //Move To Operation
        Actions actions = new Actions(driver);
        wait.until(jsLoad);
        wait.until(jQueryLoad);
        Thread.sleep(200);
        actions.moveToElement(uzmanPhotoSection).build().perform();

        //Wait for 2 second for violet color animation
        wait.until(jsLoad);
        wait.until(jQueryLoad);
        Thread.sleep(2000);

        //Take ScreenShot with AShot
        Screenshot uzmanScreenShot = takeScreenshot(uzmanPhotoSection);

        //Declare element screenshot paths
        //Concatenate with the test name.
        declareScreenShotPaths(testName+"_Baseline.png", testName+"_Actual.png", testName + "_Diff.png");

        //Write actual image to the test screenshot path
        ImageIO.write(uzmanScreenShot.getImage(), "PNG", actualImageFile);

        //Do image comparison
        doComparison(uzmanScreenShot);
    }

    //Close Driver
    @AfterClass
    public void quitDriver() {
        driver.quit();
    }
}
