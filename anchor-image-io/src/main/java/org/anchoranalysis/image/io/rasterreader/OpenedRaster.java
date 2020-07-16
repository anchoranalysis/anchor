/* (C)2020 */
package org.anchoranalysis.image.io.rasterreader;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

public abstract class OpenedRaster implements AutoCloseable {

    // Open when we don't have a specific-type
    public abstract TimeSequence open(int seriesIndex, ProgressReporter progressReporter)
            throws RasterIOException;

    // Opens a time-series as a particular type. If it's not the correct type, an error is thrown
    public TimeSequence openCheckType(
            int seriesIndex, ProgressReporter progressReporter, VoxelDataType chnlDataType)
            throws RasterIOException {

        TimeSequence ts = open(seriesIndex, progressReporter);

        if (!ts.allChnlsHaveType(chnlDataType)) {
            throw new RasterIOException(
                    String.format("File does not have dataType %s", chnlDataType));
        }

        return ts;
    }

    public abstract int numSeries();

    // Can be null if no channel names exist
    public abstract Optional<List<String>> channelNames();

    public abstract int numChnl() throws RasterIOException;

    public abstract int numFrames() throws RasterIOException;

    public abstract int bitDepth() throws RasterIOException;

    public abstract boolean isRGB() throws RasterIOException;

    public abstract void close() throws RasterIOException;

    public abstract ImageDimensions dim(int seriesIndex) throws RasterIOException;
}
