package utils;

import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeOptionsUtil {
    public ChromeOptions getChromeOptions(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("--disable-extensions");
        return options;
    }
}
