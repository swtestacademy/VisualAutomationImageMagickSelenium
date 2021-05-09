package steps;

import java.io.File;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.ashot.Screenshot;
import utils.FolderUtil;
import utils.ImageMagickUtil;
import utils.JSUtil;
import utils.ScreenshotUtil;

public class Steps {
    public WebDriver     driver;
    public WebDriverWait wait;
    public JSUtil        jsUtil;
    public String        url = "https://www.google.com";
    public Screenshot    googleScreenshot;

    public FolderUtil      folderUtil      = new FolderUtil();
    public ScreenshotUtil  screenshotUtil  = new ScreenshotUtil();
    public ImageMagickUtil imageMagickUtil = new ImageMagickUtil();

    public Steps(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 10);
        jsUtil = new JSUtil(wait, driver);
    }

    @SneakyThrows
    public void preTestSetup() {
        //Create screenshot and differences folders if they are not exist
        folderUtil.createFolder(folderUtil.parentScreenShotsLocation);
        folderUtil.createFolder(folderUtil.parentDifferencesLocation);

        //Clean Differences Root Folder
        File differencesFolder = new File(folderUtil.parentDifferencesLocation);
        FileUtils.cleanDirectory(differencesFolder);

        driver.navigate().to(url);
        jsUtil.waitJS();
        jsUtil.hideDynamicContent();
    }

    public Steps givenITakeScreenShot() {
        //Take ScreenShot with AShot
        googleScreenshot = screenshotUtil.takeScreenshot(driver);
        return this;
    }

    @SneakyThrows
    public Steps whenISaveTheScreenShotsToFolders() {
        //Write actual screenshot to the actual screenshot path
        folderUtil.writeScreenshotToFolder(googleScreenshot);
        return this;
    }

    @SneakyThrows
    public Steps thenIShouldCompareScreenshotsSuccessfully() {
        //Do image comparison
        imageMagickUtil.doComparison(googleScreenshot, folderUtil);
        return this;
    }
}
