package tests;

import org.testng.annotations.Test;

public class VisualTest extends BaseTest {
    @Test
    public void visualTest() {
        steps
            .givenITakeScreenShot()
            .whenISaveTheScreenShotsToFolders()
            .thenIShouldCompareScreenshotsSuccessfully();
    }
}
