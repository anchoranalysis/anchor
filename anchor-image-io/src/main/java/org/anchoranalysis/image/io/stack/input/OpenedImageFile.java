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

package org.anchoranalysis.image.io.stack.input;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.TimeSequence;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * An image file that has been opened for reading containing one or more series of image-{@link
 * Stack}s.
 *
 * @author Owen Feehan
 */
public interface OpenedImageFile extends AutoCloseable {

    // Opens a time-series as a particular type. If it's not the correct type, an error is thrown
    default TimeSequence openCheckType(
            int seriesIndex, Progress progress, VoxelDataType channelDataType)
            throws ImageIOException {

        TimeSequence sequence = open(seriesIndex, progress);

        if (!sequence.allChannelsHaveType(channelDataType)) {
            throw new ImageIOException(
                    String.format("File does not have dataType %s", channelDataType));
        }

        return sequence;
    }

    /** Open when we don't have a specific-type */
    TimeSequence open(int seriesIndex, Progress progress) throws ImageIOException;

    int numberSeries();

    Optional<List<String>> channelNames() throws ImageIOException;

    int numberChannels() throws ImageIOException;

    int numberFrames() throws ImageIOException;

    int bitDepth() throws ImageIOException;

    boolean isRGB() throws ImageIOException;

    void close() throws ImageIOException;

    Dimensions dimensionsForSeries(int seriesIndex) throws ImageIOException;
}
