/* (C)2020 */
package org.anchoranalysis.io.ij.bean.writer;

import java.nio.file.Path;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.generator.raster.series.ImgStackSeries;
import org.anchoranalysis.image.stack.Stack;

public abstract class IJWriterSupportsStack extends IJWriter {

    @Override
    public void writeTimeSeriesStackByte(ImgStackSeries stackSeries, Path filePath, boolean makeRGB)
            throws RasterIOException {

        try {
            Stack stack = stackSeries.createSingleImgStack();
            writeStackTime(stack, filePath, makeRGB);
        } catch (IncorrectImageSizeException e) {
            throw new RasterIOException(e);
        }
    }
}
