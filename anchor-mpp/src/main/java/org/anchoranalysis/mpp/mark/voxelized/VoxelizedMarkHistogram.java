/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.mark.voxelized;

import java.util.List;
import lombok.Getter;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParameters;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.statistics.VoxelStatistics;
import org.anchoranalysis.image.voxel.statistics.VoxelStatisticsFromHistogram;
import org.anchoranalysis.math.histogram.Histogram;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.index.IndexByChannel;
import org.anchoranalysis.mpp.index.factory.VoxelPartitionFactory;
import org.anchoranalysis.mpp.index.factory.VoxelPartitonFactoryHistogram;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

class VoxelizedMarkHistogram implements VoxelizedMark {

    private static final VoxelPartitionFactory<Histogram> FACTORY =
            new VoxelPartitonFactoryHistogram();

    // Quick access to what is inside and what is outside
    private final IndexByChannel<Histogram> partitions;

    @Getter private ObjectMask object;

    @Getter private ObjectMask objectFlattened; // null until we need it

    public VoxelizedMarkHistogram(
            Mark mark, EnergyStackWithoutParameters stack, RegionMap regionMap) {
        partitions = new IndexByChannel<>();
        initForMark(mark, stack, regionMap);
    }

    private VoxelizedMarkHistogram(VoxelizedMarkHistogram src) {
        // No duplication, only shallow copy (for now). This might change in future.
        this.partitions = src.partitions;
    }

    /** Does only a shallow copy of partition-list */
    @Override
    public VoxelizedMark duplicate() {
        return new VoxelizedMarkHistogram(this);
    }

    @Override
    public BoundedVoxels<UnsignedByteBuffer> voxels() {
        return object.boundedVoxels();
    }

    @Override
    public BoundedVoxels<UnsignedByteBuffer> voxelsMaximumIntensityProjection() {
        return objectFlattened.boundedVoxels();
    }

    @Override
    public BoundingBox boundingBox() {
        return object.boundingBox();
    }

    @Override
    public BoundingBox boundingBoxFlattened() {
        return objectFlattened.boundingBox();
    }

    @Override
    public VoxelStatistics statisticsForAllSlices(int channelID, int regionID) {
        return new VoxelStatisticsFromHistogram(
                partitions.get(channelID).getForAllSlices(regionID));
    }

    @Override
    public VoxelStatistics statisticsFor(int channelID, int regionID, int sliceID) {
        return new VoxelStatisticsFromHistogram(
                partitions.get(channelID).getForSlice(regionID, sliceID));
    }

    @Override
    public void cleanUp() {
        partitions.cleanUp(FACTORY);
    }

    @Override
    public VoxelStatistics statisticsForAllSlicesMaskSlice(
            int channelID, int regionID, int maskChannelID) {

        Histogram histogram = new Histogram(255);

        // We loop through each slice
        for (int z = 0; z < partitions.get(0).numSlices(); z++) {

            Histogram histogramChannel = partitions.get(channelID).getForSlice(regionID, z);
            Histogram histogramMask = partitions.get(maskChannelID).getForSlice(regionID, z);

            if (histogramMask.hasNonZeroCount(1)) {
                try {
                    histogram.addHistogram(histogramChannel);
                } catch (OperationFailedException e) {
                    throw new AnchorImpossibleSituationException();
                }
            }
        }
        return new VoxelStatisticsFromHistogram(histogram);
    }

    // Calculates the pixels for a mark
    private void initForMark(Mark mark, EnergyStackWithoutParameters stack, RegionMap regionMap) {

        Dimensions dimensions = stack.dimensions();
        BoundingBox box = mark.boxAllRegions(dimensions);

        ReadableTuple3i cornerMax = box.calculateCornerMaxInclusive();

        object = new ObjectMask(box);
        objectFlattened = new ObjectMask(box.flattenZ());

        Extent localExtent = box.extent();
        partitions.initialize(
                FACTORY, stack.getNumberChannels(), regionMap.numRegions(), localExtent.z());

        UnsignedByteBuffer bufferMIP = getObjectFlattened().sliceBufferLocal(0);

        for (int z = box.cornerMin().z(); z <= cornerMax.z(); z++) {

            BufferArrayList bufferArrList = new BufferArrayList();
            bufferArrList.initialize(stack, z);
            initForSlice(
                    z,
                    mark,
                    box,
                    cornerMax,
                    localExtent,
                    dimensions,
                    bufferArrList,
                    bufferMIP,
                    regionMap);
        }
    }

    private void initForSlice( // NOSONAR
            int z,
            Mark mark,
            BoundingBox box,
            ReadableTuple3i cornerMax,
            Extent localExtent,
            Dimensions dimensions,
            BufferArrayList bufferArrList,
            UnsignedByteBuffer bufferMIP,
            RegionMap regionMap) {

        Point3i running = new Point3i();
        running.setZ(z);

        int zLocal = z - box.cornerMin().z();

        List<RegionMembershipWithFlags> listRegionMembership =
                regionMap.createListMembershipWithFlags();

        UnsignedByteBuffer buffer = object.sliceBufferLocal(zLocal);

        for (int y = box.cornerMin().y(); y <= cornerMax.y(); y++) {
            running.setY(y);

            int yLocal = y - box.cornerMin().y();

            for (int x = box.cornerMin().x(); x <= cornerMax.x(); x++) {

                running.setX(x);

                int xLocal = x - box.cornerMin().x();

                int localOffset = localExtent.offset(xLocal, yLocal);
                int globalOffset = dimensions.offset(x, y);

                byte membership = mark.isPointInside(running);

                buffer.putRaw(localOffset, membership);
                bufferMIP.putRaw(localOffset, membershipMIP(membership, bufferMIP, localOffset));

                AddVoxelsToHistogram.addVoxels(
                        membership,
                        listRegionMembership,
                        partitions,
                        bufferArrList,
                        globalOffset,
                        zLocal);
            }
        }
    }

    private static byte membershipMIP(
            byte membership, UnsignedByteBuffer bufferMIP, int localOffset) {
        byte membershipMIP = bufferMIP.getRaw(localOffset);
        membershipMIP = (byte) (membershipMIP | membership);
        return membershipMIP;
    }
}
