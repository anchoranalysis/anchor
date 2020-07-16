/* (C)2020 */
package org.anchoranalysis.image.io.bean.rasterwriter;

import java.nio.file.Path;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.generator.raster.series.ImgStackSeries;
import org.anchoranalysis.image.stack.Stack;

public class NullWriter extends RasterWriter {

    @Override
    public String dfltExt() {
        return "tif";
    }

    @Override
    public void writeTimeSeriesStackByte(ImgStackSeries stackSeries, Path filePath, boolean makeRGB)
            throws RasterIOException {
        // NOTHING TO DO
    }

    @Override
    public void writeStackShort(Stack stack, Path filePath, boolean makeRGB)
            throws RasterIOException {
        // NOTHING TO DO
    }

    @Override
    public void writeStackByte(Stack stack, Path filePath, boolean makeRGB)
            throws RasterIOException {
        // NOTHING TO DO
    }
}
