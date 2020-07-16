/* (C)2020 */
package org.anchoranalysis.image.io.bean.rasterreader;

import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.stack.Stack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RasterReaderUtilities {

    public static Stack openStackFromPath(RasterReader rasterReader, Path path)
            throws RasterIOException {
        OpenedRaster openedRaster = rasterReader.openFile(path);

        try {
            if (openedRaster.numSeries() != 1) {
                throw new RasterIOException("there must be exactly one series");
            }

            Stack stack = openedRaster.open(0, ProgressReporterNull.get()).get(0);
            return stack.duplicate();
        } finally {
            openedRaster.close();
        }
    }

    public static Mask openBinaryChnl(RasterReader rasterReader, Path path, BinaryValues bv)
            throws RasterIOException {

        Stack stack = openStackFromPath(rasterReader, path);

        if (stack.getNumChnl() != 1) {
            throw new RasterIOException(
                    String.format(
                            "There must be exactly one channel, but there are %d",
                            stack.getNumChnl()));
        }

        return new Mask(stack.getChnl(0), bv);
    }
}
