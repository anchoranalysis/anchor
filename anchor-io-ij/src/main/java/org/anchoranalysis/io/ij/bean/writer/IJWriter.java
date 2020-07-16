/* (C)2020 */
package org.anchoranalysis.io.ij.bean.writer;

import ij.ImagePlus;
import ij.io.FileSaver;
import java.nio.file.Path;
import org.anchoranalysis.image.convert.IJWrap;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterwriter.RasterWriter;
import org.anchoranalysis.image.stack.Stack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//
//  Note the difference between ImageStack (and ImageJ class)
//   and ImgStack (one of our classes)
//
public abstract class IJWriter extends RasterWriter {

    private static Log log = LogFactory.getLog(IJWriter.class);

    @Override
    public void writeStackByte(Stack stack, Path filePath, boolean makeRGB)
            throws RasterIOException {
        writeStackTimeCheck(stack, filePath, makeRGB);
    }

    @Override
    public void writeStackShort(Stack stack, Path filePath, boolean makeRGB)
            throws RasterIOException {
        writeStackTimeCheck(stack, filePath, false);
    }

    protected abstract boolean writeRaster(FileSaver fs, String outPath, boolean asStack)
            throws RasterIOException;

    private void writeStackTimeCheck(Stack stack, Path filePath, boolean makeRGB)
            throws RasterIOException {

        if (!(stack.getNumChnl() == 1 || stack.getNumChnl() == 3)) {
            throw new RasterIOException("Stack must have 1 or 3 channels");
        }

        if (makeRGB && (stack.getNumChnl() != 3)) {
            throw new RasterIOException(
                    "To make an RGB image, the stack must have exactly 3 channels");
        }

        writeStackTime(stack, filePath, makeRGB);
    }

    protected void writeStackTime(Stack stack, Path filePath, boolean makeRGB)
            throws RasterIOException {

        log.debug(String.format("Writing image %s", filePath));

        ImageDimensions sd = stack.getChnl(0).getDimensions();

        ImagePlus imp = IJWrap.createImagePlus(stack, makeRGB);

        writeImagePlus(imp, filePath, (stack.getChnl(0).getDimensions().getZ() > 1));

        imp.close();

        assert (imp.getNSlices() == sd.getZ());

        log.debug(String.format("Finished writing image %s", filePath));
    }

    private void writeImagePlus(ImagePlus imp, Path filePath, boolean asStack)
            throws RasterIOException {

        FileSaver fs = new FileSaver(imp);
        if (!writeRaster(fs, filePath.toString(), asStack)) {
            throw new RasterIOException(
                    String.format("An error occured in IJ writing file '%s'", filePath));
        }
    }
}
