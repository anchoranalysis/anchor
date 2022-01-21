/*-
 * #%L
 * anchor-io-bioformats
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.io.bioformats.copyconvert;

import java.io.IOException;
import lombok.Value;

/**
 * How values are encoded into an image-file.
 *
 * @author Owen Feehan
 */
@Value
public class ImageFileEncoding {

    /**
     * Whether the source bytes encoded as RGB (i.e. with the three colors coded into a 4 bytes
     * coding per voxel).
     */
    private boolean rgb;

    /**
     * Whether the channels are interleaved
     *
     * <p>Interleaving means that voxels from successive channels are directly adjacent. Otherwise,
     * each channel's voxels are contiguous.
     */
    private boolean interleaved;

    /**
     * The total number of channels found in any one buffer.
     *
     * <p>For an RGB image, this is by definition three.
     */
    private int numberChannelsPerArray;

    /**
     * When RGB-encoded and non-interleaved, the source array is considered 1 channel (not three).
     *
     * <p>When not RGB encoded, it describes the number of interleaved channels present.
     *
     * @return the number of channels as per above, which must always be > 0.
     * @throws IOException if numberChannelsPerArray is zero, and the image is either non-RGB or
     *     interleaved.
     */
    public int numberDistinctChannelsSource() throws IOException {
        if (rgb && !interleaved) {
            return 1;
        } else {
            if (numberChannelsPerArray == 0) {
                throw new IOException(
                        String.format(
                                "numberChannelsPerArray must be positive, but is rather %d",
                                numberChannelsPerArray));
            }
            return numberChannelsPerArray;
        }
    }
}
