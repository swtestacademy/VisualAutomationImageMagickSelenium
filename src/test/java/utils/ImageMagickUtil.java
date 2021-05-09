package utils;

import com.google.common.io.Files;
import javax.imageio.ImageIO;
import org.im4java.core.CompareCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.StandardStream;
import ru.yandex.qatools.ashot.Screenshot;

public class ImageMagickUtil {
    //ImageMagick Compare Method
    public void compareImagesWithImageMagick(String expected, String actual, String difference, FolderUtil folderUtil) throws Exception {
        // This class implements the processing of os-commands using a ProcessBuilder.
        // This is the core class of the im4java-library where all the magic takes place.
        //ProcessStarter.setGlobalSearchPath("C:\\Program Files\\ImageMagick-7.0.4-Q16");

        // This instance wraps the compare command
        CompareCmd compare = new CompareCmd();

        // Set the ErrorConsumer for the stderr of the ProcessStarter.
        compare.setErrorConsumer(StandardStream.STDERR);

        // Create ImageMagick Operation Object
        IMOperation cmpOp = new IMOperation();

        //Add option -fuzz to the ImageMagick commandline
        //With Fuzz we can ignore small changes
        cmpOp.fuzz(8.0);

        //The special "-metric" setting of 'AE' (short for "Absolute Error" count), will report (to standard error),
        //a count of the actual number of pixels that were masked, at the current fuzz factor.
        cmpOp.metric("AE");

        // Add the expected image
        cmpOp.addImage(expected);

        // Add the actual image
        cmpOp.addImage(actual);

        // This stores the difference
        cmpOp.addImage(difference);

        try {
            //Do the compare
            System.out.println("Comparison Started!");
            compare.run(cmpOp);
            System.out.println("Comparison Finished!");
        }
        catch (Exception ex) {
            System.out.print(ex);
            System.out.println("Comparison Failed!");
            //Put the difference image to the global differences folder
            Files.copy(folderUtil.differenceImageFile, folderUtil.differenceFileForParent);
            throw ex;
        }
    }

    //Compare Operation
    public void doComparison(Screenshot elementScreenShot, FolderUtil folderUtil) throws Exception {
        //Did we capture baseline image before?
        if (folderUtil.baselineImageFile.exists()) {
            //Compare screenshot with baseline
            System.out.println("Comparison method will be called!\n");

            System.out.println("Baseline: " + folderUtil.baselineScreenShotPath + "\n" +
                "Actual: " + folderUtil.actualScreenShotPath + "\n" +
                "Diff: " + folderUtil.differenceScreenShotPath);

            //Try to use IM4Java for comparison
            compareImagesWithImageMagick(folderUtil.baselineScreenShotPath, folderUtil.actualScreenShotPath,
                folderUtil.differenceScreenShotPath, folderUtil);
        } else {
            System.out.println("BaselineScreenshot is not exist! We put it into test screenshot folder.\n");
            //Put the screenshot to the specified folder
            ImageIO.write(elementScreenShot.getImage(), "PNG", folderUtil.baselineImageFile);
        }
    }
}
