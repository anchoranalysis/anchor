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

import java.io.IOException;
import java.nio.ByteBuffer;
import loci.formats.FormatException;
import loci.formats.IFormatWriter;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;

class RGBWriterByte extends RGBWriter {

    public RGBWriterByte(IFormatWriter writer, Stack stack, boolean plusAlpha) {
        super(writer, stack, plusAlpha);
    }

    @Override
    protected void mergeSliceAsRGB(int z, int capacity) throws ImageIOException {

        ByteBuffer merged = ByteBuffer.allocate(capacity * numberChannels());
        putSlice(merged, channelRed, z);
        putSlice(merged, channelGreen, z);
        putSlice(merged, channelBlue, z);
        if (channelAlpha.isPresent()) {
            putSlice(merged, channelAlpha.get(), z);
        }

        try {
            writer.saveBytes(z, merged.array());
        } catch (FormatException | IOException e) {
            throw new ImageIOException("Failed to merge-slices as RGB", e);
        }
    }

    private static void putSlice(ByteBuffer merged, Channel channel, int z) {
        ByteBuffer source = channel.voxels().asByte().sliceBuffer(z).getDelegate();
        source.rewind();
        merged.put(source);
    }
}
