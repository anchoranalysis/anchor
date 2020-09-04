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

import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import java.util.List;
import lombok.Getter;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParams;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.statistics.VoxelStatistics;
import org.anchoranalysis.image.voxel.statistics.VoxelStatisticsFromHistogram;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.pixelpart.IndexByChannel;
import org.anchoranalysis.mpp.pixelpart.factory.PixelPartFactory;
import org.anchoranalysis.mpp.pixelpart.factory.PixelPartFactoryHistogram;

class VoxelizedMarkHistogram implements VoxelizedMark {

    private static final PixelPartFactory<Histogram> FACTORY = new PixelPartFactoryHistogram();

    // Quick access to what is inside and what is outside
    private final IndexByChannel<Histogram> partitionList;

    @Getter private ObjectMask object;

    @Getter private ObjectMask objectFlattened; // null until we need it

    public VoxelizedMarkHistogram(Mark mark, EnergyStackWithoutParams stack, RegionMap regionMap) {
        partitionList = new IndexByChannel<>();
        initForMark(mark, stack, regionMap);
    }

    private VoxelizedMarkHistogram(VoxelizedMarkHistogram src) {
        // No duplication, only shallow copy (for now). This might change in future.
        this.partitionList = src.partitionList;
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
                partitionList.get(channelID).getForAllSlices(regionID));
    }

    @Override
    public VoxelStatistics statisticsFor(int channelID, int regionID, int sliceID) {
        return new VoxelStatisticsFromHistogram(
                partitionList.get(channelID).getForSlice(regionID, sliceID));
    }

    @Override
    public void cleanUp() {
        partitionList.cleanUp(FACTORY);
    }

    @Override
    public VoxelStatistics statisticsForAllSlicesMaskSlice(
            int channelID, int regionID, int maskChannelID) {

        Histogram histogram = new Histogram(255);

        // We loop through each slice
        for (int z = 0; z < partitionList.get(0).numSlices(); z++) {

            Histogram hChannel = partitionList.get(channelID).getForSlice(regionID, z);
            Histogram hMaskChannel = partitionList.get(maskChannelID).getForSlice(regionID, z);

            if (hMaskChannel.hasAboveZero()) {
                try {
                    histogram.addHistogram(hChannel);
                } catch (OperationFailedException e) {
                    throw new AnchorImpossibleSituationException();
                }
            }
        }
        return new VoxelStatisticsFromHistogram(histogram);
    }

    // Calculates the pixels for a mark
    private void initForMark(Mark mark, EnergyStackWithoutParams stack, RegionMap regionMap) {

        Dimensions dimensions = stack.dimensions();
        BoundingBox box = mark.boxAllRegions(dimensions);

        ReadableTuple3i cornerMax = box.calculateCornerMax();

        object = new ObjectMask(box);
        objectFlattened = new ObjectMask(box.flattenZ());

        Extent localExtent = box.extent();
        partitionList.init(
                FACTORY, stack.getNumberChannels(), regionMap.numRegions(), localExtent.z());

        UnsignedByteBuffer bufferMIP = getObjectFlattened().sliceBufferLocal(0);

        for (int z = box.cornerMin().z(); z <= cornerMax.z(); z++) {

            BufferArrayList bufferArrList = new BufferArrayList();
            bufferArrList.init(stack, z);
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

        Point3d running = new Point3d();
        running.setZ(z + 0.5);

        int zLocal = z - box.cornerMin().z();

        List<RegionMembershipWithFlags> listRegionMembership =
                regionMap.createListMembershipWithFlags();

        UnsignedByteBuffer buffer = object.sliceBufferLocal(zLocal);

        for (int y = box.cornerMin().y(); y <= cornerMax.y(); y++) {
            running.setY(y + 0.5);

            int yLocal = y - box.cornerMin().y();

            for (int x = box.cornerMin().x(); x <= cornerMax.x(); x++) {

                running.setX(x + 0.5);

                int xLocal = x - box.cornerMin().x();

                int localOffset = localExtent.offset(xLocal, yLocal);
                int globalOffset = dimensions.offset(x, y);

                byte membership = mark.isPointInside(new Point3d(running));

                buffer.put(localOffset, membership);
                bufferMIP.put(localOffset, membershipMIP(membership, bufferMIP, localOffset));

                AddVoxelsToHistogram.addVoxels(
                        membership,
                        listRegionMembership,
                        partitionList,
                        bufferArrList,
                        globalOffset,
                        zLocal);
            }
        }
    }

    private static byte membershipMIP(byte membership, UnsignedByteBuffer bufferMIP, int localOffset) {
        byte membershipMIP = bufferMIP.get(localOffset);
        membershipMIP = (byte) (membershipMIP | membership);
        return membershipMIP;
    }
}
