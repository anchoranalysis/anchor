/* (C)2020 */
package org.anchoranalysis.anchor.mpp.pxlmark;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;
import org.anchoranalysis.image.voxel.statistics.VoxelStatistics;

/**
 * A voxelized representation of a Mark i.e. a mark turned into voxels.
 *
 * @author Owen Feehan
 */
public interface VoxelizedMark {

    BoundedVoxelBox<ByteBuffer> getVoxelBox();

    BoundedVoxelBox<ByteBuffer> getVoxelBoxMIP();

    BoundingBox getBoundingBox();

    BoundingBox getBoundingBoxMIP();

    VoxelizedMark duplicate();

    VoxelStatistics statisticsForAllSlices(int chnlID, int regionID);

    VoxelStatistics statisticsForAllSlicesMaskSlice(int chnlID, int regionID, int maskChnlID);

    VoxelStatistics statisticsFor(int chnlID, int regionID, int sliceID);

    void cleanUp();
}
