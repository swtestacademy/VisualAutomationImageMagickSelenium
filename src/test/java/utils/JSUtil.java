package utils;

import java.util.function.Function;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class JSUtil {

    public WebDriverWait wait;
    public WebDriver     driver;
    public JavascriptExecutor js;

    public JSUtil(WebDriverWait wait, WebDriver driver) {
        this.wait = wait;
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
    }

    public void waitJS() {
        //Wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = driver -> ((JavascriptExecutor) driver)
            .executeScript("return document.readyState").toString().equals("complete");

        if (!(Boolean) js.executeScript("return (typeof jQuery != \"undefined\")")) {
            js.executeScript(
                "var headID = document.getElementsByTagName('head')[0];" +
                    "var newScript = document.createElement('script');" +
                    "newScript.type = 'text/javascript';" +
                    "newScript.src = 'https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js';" +
                    "headID.appendChild(newScript);");
            WebDriverWait waitJQ = new WebDriverWait(driver, 30);
            Function<WebDriver, Boolean> jQueryAvailable = WebDriver -> (
                (Boolean) js.executeScript("return (typeof jQuery != \"undefined\")")
            );
            waitJQ.until(jQueryAvailable);
        }

        //JQuery Wait
        ExpectedCondition<Boolean> jQueryLoad = driver -> ((Long) ((JavascriptExecutor) driver).executeScript("return jQuery.active") == 0);

        wait.until(jsLoad);
        wait.until(jQueryLoad);
    }

    public void hideDynamicContent() {
        //Hide dynamic elements inside search box
        js.executeScript("document.querySelector('div[class=\\'SDkEP\\']').style.display='none'");
    }
}
