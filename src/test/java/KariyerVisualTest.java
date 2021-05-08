import java.awt.image.BufferedImage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class KariyerVisualTest extends BaseTest {
    @Test
    public void kariyerUzmanCssTest () throws Exception {
        //Handle popup
        handlePopup(".ui-dialog-titlebar-close");

//        //Close banner
        closeBanner();

        //Declare UZMAN photo section
        WebElement uzmanPhotoSection = driver.findElement(By.cssSelector(".item.uzman"));

        //Unhide Text (It is Changing A lot)
        unhideElement("document.getElementsByClassName('count')[0].style.display='none';");

        //Move To Operation
        moveToElement(uzmanPhotoSection);

        //Wait for 2 second for violet color animation
        Thread.sleep(2000);

        //Take ScreenShot with AShot
//        Screenshot uzmanScreenShot = takeScreenshot(uzmanPhotoSection);
        BufferedImage uzmanScreenShot = takeScreenshot2(uzmanPhotoSection);

        //Write actual screenshot to the actual screenshot path
//        writeScreenshotToFolder(uzmanScreenShot);
        writeScreenshotToFolder(uzmanScreenShot);

        //Do image comparison
//        doComparison(uzmanScreenShot);
        doComparison(uzmanScreenShot);
    }
}
