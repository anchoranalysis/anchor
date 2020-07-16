/* (C)2020 */
package org.anchoranalysis.image.io.bean.rasterwriter;

import java.nio.file.Path;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.channel.factory.ChannelFactoryShort;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.generator.raster.series.ImgStackSeries;
import org.anchoranalysis.image.stack.Stack;

public abstract class RasterWriter extends AnchorBean<RasterWriter> {

    // Get the default extension for a writer
    public abstract String dfltExt();

    public abstract void writeTimeSeriesStackByte(
            ImgStackSeries stackSeries, Path filePath, boolean makeRGB) throws RasterIOException;

    public void writeStack(Stack stack, Path filePath, boolean makeRGB) throws RasterIOException {

        if (stack.allChnlsHaveType(ChannelFactoryByte.staticDataType())) {
            writeStackByte((Stack) stack, filePath, makeRGB);
        } else if (stack.allChnlsHaveType(ChannelFactoryShort.staticDataType())) {
            writeStackShort((Stack) stack, filePath, makeRGB);
        } else {
            throw new RasterIOException(
                    "Channels in ImgStack are neither homogenously unsigned 8-bit (byte) or unsigned 16-bit (short). Other combinations unsupported");
        }
    }

    public abstract void writeStackByte(Stack stack, Path filePath, boolean makeRGB)
            throws RasterIOException;

    public abstract void writeStackShort(Stack stack, Path filePath, boolean makeRGB)
            throws RasterIOException;
}
