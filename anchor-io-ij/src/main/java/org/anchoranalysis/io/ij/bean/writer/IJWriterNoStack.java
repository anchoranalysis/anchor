/* (C)2020 */
package org.anchoranalysis.io.ij.bean.writer;

import java.nio.file.Path;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.generator.raster.series.ImgStackSeries;

public abstract class IJWriterNoStack extends IJWriter {

    @Override
    public void writeTimeSeriesStackByte(ImgStackSeries stackSeries, Path filePath, boolean makeRGB)
            throws RasterIOException {
        throw new RasterIOException("Writing as a time-series is unsupported for this format");
    }
}
