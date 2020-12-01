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

package org.anchoranalysis.io.bioformats.bean.options;

import java.util.List;
import java.util.Optional;
import loci.formats.IFormatReader;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.io.bioformats.bean.BioformatsReader;

/**
 * Options that influence how stack is read in {@link BioformatsReader}.
 * 
 * @author Owen Feehan
 *
 */
public abstract class ReadOptions extends AnchorBean<ReadOptions> {

    /** 
     * Number of channels.
     *
     * @param reader the bioformats reader
     * @return the number of channels
     */
    public abstract int sizeC(IFormatReader reader);

    /** 
     * Number of time-points (frames in a time series).
     *
     * @param reader the bioformats reader
     * @return the number of channels
     */
    public abstract int sizeT(IFormatReader reader);

    /** 
     * Number of z-slices (slices in a 3D image).
     *
     * @param reader the bioformats reader
     * @return the number of slices
     */
    public abstract int sizeZ(IFormatReader reader);

    /**
     * The number of bits used per pixel.
     * 
     * <p>This may be a smaller number than the size of the voxel's data-type
     * e.g. a 16-bit format may be used to store only 12-bits of image data.
     * 
     * @param reader the bioformats reader
     * @return the number of bits used per pixel.
     */
    public abstract int effectiveBitsPerPixel(IFormatReader reader);

    /**
     * The number of channels returned with each call to {@link IFormatReader#openBytes(int)}.
     * 
     * @param reader the bioformats reader
     * @return the number of channels returned with each call to openBytes.
     */
    public abstract int channelsPerByteArray(IFormatReader reader);

    /** 
     * Is it an image with three channels (red, green and blue)?
     * 
     * @param reader the bioformats reader
     * @return true iff its a RGB image
     */
    public abstract boolean isRGB(IFormatReader reader);

    /** 
     * A list of channel-names, if available.
     * 
     * <p>The order matches the channel indexing e.g. the first name in the list
     * corresponds to the channel with {@code index=0}.
     * 
     * @param reader the bioformats reader
     * @return the channel-names, if available. 
     */
    public abstract Optional<List<String>> determineChannelNames(IFormatReader reader);
}
