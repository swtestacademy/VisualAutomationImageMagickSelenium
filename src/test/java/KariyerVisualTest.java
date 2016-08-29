import org.im4java.core.CompareCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessStarter;
import org.im4java.process.StandardStream;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
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
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by onurb on 28-Aug-16.
 */
public class KariyerVisualTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private String url = "http://www.kariyer.net";
    private String currentDir = System.getProperty("user.dir");
    //Main screenshot directory
    private String parentScreenShotsLocation = currentDir + "\\ScreenShots\\";
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

    //Setup Driver
    @BeforeClass
    public void setupTestClass() {
        //Declare Firefox driver
        driver = new FirefoxDriver();

        //Go to URL
        driver.navigate().to(url);

        //Maximize the browser
        driver.manage().window().maximize();

        //Declare a 10 seconds wait time
        wait = new WebDriverWait(driver,10);

        //Create screenshot parent folder if not exist
        createFolder(parentScreenShotsLocation);

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
        //Screenshot firstPhotoScreenshot = new AShot().takeScreenshot(driver, first_photo);
        Screenshot elementScreenShot = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .takeScreenshot(driver,element);

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
    }

    //ImageMagick Compare Method
    private boolean compareImagesWithImageMagick (String exp, String cur, String diff) throws Exception {
        ProcessStarter.setGlobalSearchPath("C:\\Program Files\\ImageMagick-7.0.2-Q16");

        // This instance wraps the compare command
        CompareCmd compare = new CompareCmd();

        // For metric-output
        compare.setErrorConsumer(StandardStream.STDERR);
        IMOperation cmpOp = new IMOperation();

        //Set the compare metric
        //cmpOp.metric("AE").fuzz(0.5);
        cmpOp.fuzz(0.5);
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
    public void VisualTestForElement() throws Exception {
        //Handle popup
        handlePopup(".ui-dialog-titlebar-close");

       //Declare UZMAN photo section
        WebElement uzmanPhotoSection = driver.findElement(By.cssSelector(".item.uzman"));

        //Move To Operation
        Actions actions = new Actions(driver);
        actions.moveToElement(uzmanPhotoSection).perform();

        //Wait for 3 second for violet color animation
        Thread.sleep(3000);

        //Take ScreenShot with AShot
        Screenshot uzmanScreenShot = takeScreenshot(uzmanPhotoSection);

        //Declare element screenshot paths
        declareScreenShotPaths("uzmanBaseline.png", "uzmanActual.png", "uzmanDiff.png");

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
