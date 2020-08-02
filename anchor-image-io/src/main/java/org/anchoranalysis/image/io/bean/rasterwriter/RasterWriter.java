/*-
 * #%L
 * anchor-image-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.image.io.bean.rasterwriter;

import java.nio.file.Path;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.channel.factory.ChannelFactoryShort;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.generator.raster.series.StackSeries;
import org.anchoranalysis.image.stack.Stack;

public abstract class RasterWriter extends AnchorBean<RasterWriter> {

    // Get the default extension for a writer
    public abstract String dfltExt();

    public abstract void writeTimeSeriesStackByte(
            StackSeries stackSeries, Path filePath, boolean makeRGB) throws RasterIOException;

    public void writeStack(Stack stack, Path filePath, boolean makeRGB) throws RasterIOException {

        if (stack.allChannelsHaveType(ChannelFactoryByte.staticDataType())) {
            writeStackByte((Stack) stack, filePath, makeRGB);
        } else if (stack.allChannelsHaveType(ChannelFactoryShort.staticDataType())) {
            writeStackShort((Stack) stack, filePath, makeRGB);
        } else {
            throw new RasterIOException(
                    "Channels in stack are neither homogenously unsigned 8-bit (byte) or unsigned 16-bit (short). Other combinations unsupported");
        }
    }

    public abstract void writeStackByte(Stack stack, Path filePath, boolean makeRGB)
            throws RasterIOException;

    public abstract void writeStackShort(Stack stack, Path filePath, boolean makeRGB)
            throws RasterIOException;
}
