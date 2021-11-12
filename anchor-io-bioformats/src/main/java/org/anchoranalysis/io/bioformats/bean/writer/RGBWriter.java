/*-
 * #%L
 * anchor-plugin-io
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
package org.anchoranalysis.io.bioformats.bean.writer;

import loci.formats.IFormatWriter;
import java.util.Optional;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;

abstract class RGBWriter {

    /** The writer to eventual write the image. */
    protected final IFormatWriter writer;
    
    /** The red channel. */
    protected final Channel channelRed;
    
    /** The blue channel. */
    protected final Channel channelBlue;
    
    /** The green channel. */
    protected final Channel channelGreen;
    
    /** The alpha channel, if one is defined. */
    protected final Optional<Channel> channelAlpha;

    protected RGBWriter(IFormatWriter writer, Stack stack, boolean plusAlpha) {
        this.writer = writer;
        this.channelRed = stack.getChannel(0);
        this.channelGreen = stack.getChannel(1);
        this.channelBlue = stack.getChannel(2);
        this.channelAlpha = OptionalUtilities.createFromFlag(plusAlpha, () -> stack.getChannel(3));
    }

    public void writeAsRGB() throws ImageIOException {

        int capacity = channelRed.voxels().any().extent().areaXY();

        channelRed.extent().iterateOverZ(z -> mergeSliceAsRGB(z, capacity));
    }
    
    /**
     * The total number of channels.
     * 
     * @return 4 if an alpha channel is present, or 3 if it is not.
     */
    protected int numberChannels() {
        return channelAlpha.isPresent() ? 4 : 3;
    }

    protected abstract void mergeSliceAsRGB(int z, int capacity) throws ImageIOException;
}
