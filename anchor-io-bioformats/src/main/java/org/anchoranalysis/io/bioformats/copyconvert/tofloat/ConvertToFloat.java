/*-
 * #%L
 * anchor-io-bioformats
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

package org.anchoranalysis.io.bioformats.copyconvert.tofloat;

import java.io.IOException;
import java.nio.FloatBuffer;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferFloat;
import org.anchoranalysis.io.bioformats.copyconvert.ConvertTo;

public abstract class ConvertToFloat extends ConvertTo<FloatBuffer> {

    private int sizeBytesChnl;
    private ImageDimensions sd;

    public ConvertToFloat() {
        super(VoxelBoxWrapper::asFloat);
    }

    protected abstract int bytesPerPixel();

    @Override
    protected void setupBefore(ImageDimensions sd, int numChnlsPerByteArray) {
        sizeBytesChnl = sd.getX() * sd.getY() * bytesPerPixel();
        this.sd = sd;
    }

    @Override
    protected VoxelBuffer<FloatBuffer> convertSingleChnl(byte[] src, int channelRelative)
            throws IOException {
        int index = (sizeBytesChnl * channelRelative);
        float[] fArr = convertIntegerBytesToFloatArray(sd, src, index);
        return VoxelBufferFloat.wrap(fArr);
    }

    protected abstract float[] convertIntegerBytesToFloatArray(
            ImageDimensions sd, byte[] src, int srcOffset) throws IOException;
}
