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

package org.anchoranalysis.image.io.rasterreader;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

public interface OpenedRaster extends AutoCloseable {

    // Opens a time-series as a particular type. If it's not the correct type, an error is thrown
    default TimeSequence openCheckType(
            int seriesIndex, ProgressReporter progressReporter, VoxelDataType chnlDataType)
            throws RasterIOException {

        TimeSequence ts = open(seriesIndex, progressReporter);

        if (!ts.allChnlsHaveType(chnlDataType)) {
            throw new RasterIOException(
                    String.format("File does not have dataType %s", chnlDataType));
        }

        return ts;
    }

    /** Open when we don't have a specific-type */
    TimeSequence open(int seriesIndex, ProgressReporter progressReporter) throws RasterIOException;

    int numberSeries();

    // Can be null if no channel names exist
    Optional<List<String>> channelNames();

    int numberChannels() throws RasterIOException;

    int numberFrames() throws RasterIOException;

    int bitDepth() throws RasterIOException;

    boolean isRGB() throws RasterIOException;

    void close() throws RasterIOException;

    ImageDimensions dimensionsForSeries(int seriesIndex) throws RasterIOException;
}
