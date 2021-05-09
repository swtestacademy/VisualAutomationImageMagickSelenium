package utils;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import ru.yandex.qatools.ashot.Screenshot;

public class FolderUtil {
    //Main Directory of the test code
    public String currentDir = System.getProperty("user.dir");

    //Test name
    public String testName;

    //Test Screenshot directory
    public String testScreenShotDirectory;

    //Main screenshot directory
    public String parentScreenShotsLocation = currentDir + "/ScreenShots/";

    //Main differences directory
    public String parentDifferencesLocation = currentDir + "/Differences/";

    //Element screenshot paths
    public String baselineScreenShotPath;
    public String actualScreenShotPath;
    public String differenceScreenShotPath;

    //Image files
    public File baselineImageFile;
    public File actualImageFile;
    public File differenceImageFile;
    public File differenceFileForParent;

    public void setUpFilesAndFolders(String name) {
        //Get the test name to create a specific screenshot folder for each test.
        testName = name;
        System.out.println("Test Name: " + testName + "\n");

        //Create a specific directory for a test
        testScreenShotDirectory = parentScreenShotsLocation + testName + "/";
        createFolder(testScreenShotDirectory);

        //Declare element screenshot paths, concatenate with the test name.
        declareScreenShotPaths(testName + "_Baseline.png", testName + "_Actual.png", testName + "_Diff.png");
    }

    //Create Folder Method
    public void createFolder(String path) {
        File testDirectory = new File(path);
        if (!testDirectory.exists()) {
            if (testDirectory.mkdir()) {
                System.out.println("Directory: " + path + " is created!");
            } else {
                System.out.println("Failed to create directory: " + path);
            }
        } else {
            System.out.println("Directory already exists: " + path);
        }
    }

    //Write
    public void writeScreenshotToFolder(Screenshot screenshot) throws IOException {
        ImageIO.write(screenshot.getImage(), "PNG", actualImageFile);
    }

    //Screenshot paths
    public void declareScreenShotPaths(String baseline, String actual, String diff) {
        //BaseLine, Actual, Difference Photo Paths
        baselineScreenShotPath = testScreenShotDirectory + baseline;
        actualScreenShotPath = testScreenShotDirectory + actual;
        differenceScreenShotPath = testScreenShotDirectory + diff;

        //BaseLine, Actual Photo Files
        baselineImageFile = new File(baselineScreenShotPath);
        actualImageFile = new File(actualScreenShotPath);
        differenceImageFile = new File(differenceScreenShotPath);

        //For copying difference to the parent Difference Folder
        differenceFileForParent = new File(parentDifferencesLocation + diff);
    }
}
