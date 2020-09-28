package org.anchoranalysis.image.io.bean.rasterwriter;

import java.nio.file.Path;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.generator.raster.series.StackSeries;
import org.anchoranalysis.image.io.rasterwriter.RasterWriteOptions;
import org.anchoranalysis.image.stack.Stack;

/**
 * A base-class for a raster-writer that writes only one or three channeled images, and a flexible
 * extension.
 *
 * @author Owen Feehan
 */
public abstract class OneOrThreeChannelsWriter extends RasterWriter {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String extension = "png";
    // END BEAN PROPERTIES

    @Override
    public String fileExtension(RasterWriteOptions writeOptions) {
        return extension;
    }

    @Override
    public void writeStackSeries(
            StackSeries stackSeries,
            Path filePath,
            boolean makeRGB,
            RasterWriteOptions writeOptions)
            throws RasterIOException {
        throw new RasterIOException("Writing time-series is unsupported for this format");
    }

    @Override
    public void writeStack(
            Stack stack, Path filePath, boolean makeRGB, RasterWriteOptions writeOptions)
            throws RasterIOException {

        if (stack.getNumberChannels() == 3 && !makeRGB) {
            throw new RasterIOException("3-channel images can only be created as RGB");
        }

        writeStackAfterCheck(stack, filePath);
    }

    protected abstract void writeStackAfterCheck(Stack stack, Path filePath)
            throws RasterIOException;
}
