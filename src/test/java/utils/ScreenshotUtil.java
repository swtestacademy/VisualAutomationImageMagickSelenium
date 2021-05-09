package utils;

import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;

public class ScreenshotUtil {
    //Take Screenshot with AShot
    public Screenshot takeScreenshot(WebDriver driver) {
        Screenshot screenshot = new AShot().takeScreenshot(driver);
        //Print element size
        String size = "Height: " + screenshot.getImage().getHeight() + "\n" + "Width: " + screenshot.getImage().getWidth() + "\n";
        System.out.print("Size: " + size);
        return screenshot;
    }
}
