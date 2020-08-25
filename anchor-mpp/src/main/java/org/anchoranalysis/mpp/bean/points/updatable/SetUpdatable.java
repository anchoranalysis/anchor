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

package org.anchoranalysis.mpp.bean.points.updatable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembership;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.mpp.mark.set.UpdateMarkSetException;
import org.anchoranalysis.mpp.mark.voxelized.VoxelizedMark;
import org.anchoranalysis.mpp.mark.voxelized.memo.MemoForIndex;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.mpp.overlap.OverlapUtilities;

public class SetUpdatable extends UpdatablePointsContainer {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private int regionID = GlobalRegionIdentifiers.SUBMARK_INSIDE;
    // END BEAN PROPERTIES

    private RandomSet<Point3d> setPoints;

    private Dimensions dimensions;
    private Mask mask;
    private Channel maskChannel;

    @Override
    public void init(Mask mask) throws InitException {
        this.mask = mask;
        this.maskChannel = mask.channel();

        dimensions = mask.dimensions();

        setPoints = new RandomSet<>();

        BinaryValuesByte bvb = mask.binaryValues().createByte();
        addEntireScene(bvb);
    }

    // Randomise location
    @Override
    public Optional<Point3d> sample(RandomNumberGenerator randomNumberGenerator) {

        if (setPoints.isEmpty()) {
            return Optional.empty();
        }

        Point3d point = sampleFromSet(setPoints, randomNumberGenerator);

        assert (point.x() >= 0);
        assert (point.y() >= 0);
        assert (point.z() >= 0);

        assert (point.x() < dimensions.x());
        assert (point.y() < dimensions.y());
        assert (point.z() < dimensions.z());

        // To hide our internal data from manipulation, even though this (presumably) adds
        //  a bit of overhead
        return Optional.of(new Point3d(point));
    }

    @Override
    public void initUpdatableMarkSet(
            MemoForIndex marks,
            EnergyStack energyStack,
            Logger logger,
            SharedFeatureMulti sharedFeatures)
            throws InitException {
        // NOTHING TO DO
    }

    private void addEntireScene(BinaryValuesByte bvb) {

        Extent extent = maskChannel.extent();

        Voxels<ByteBuffer> voxelsBinary = maskChannel.voxels().asByte();

        // Where we actually do the work
        Point3i position = new Point3i();

        for (position.setZ(0); position.z() < dimensions.z(); position.incrementZ()) {

            ByteBuffer bbBinaryImage = voxelsBinary.sliceBuffer(position.z());

            for (position.setY(0); position.y() < dimensions.y(); position.incrementY()) {
                for (position.setX(0); position.x() < dimensions.x(); position.incrementX()) {

                    if (bbBinaryImage.get(extent.offsetSlice(position)) == bvb.getOnByte()) {
                        setPoints.add(PointConverter.doubleFromInt(position));
                    }
                }
            }
        }
    }

    public Set<Point3d> getSetPoints() {
        return setPoints;
    }

    @Override
    public Dimensions dimensions() {
        return dimensions;
    }

    @Override
    public void add(MemoForIndex marksExisting, VoxelizedMarkMemo newMark)
            throws UpdateMarkSetException {
        removePointsInMark(newMark);
    }

    private void rmvPoint(ReadableTuple3i crntExtentPoint, ReadableTuple3i crnrPoint) {
        int xGlobal = crnrPoint.x() + crntExtentPoint.x();
        int yGlobal = crnrPoint.y() + crntExtentPoint.y();
        int zGlobal = crnrPoint.z() + crntExtentPoint.z();

        Point3d pointGlobal = new Point3d(xGlobal, yGlobal, zGlobal);

        setPoints.remove(pointGlobal);
    }

    public void removePointsInMark(VoxelizedMarkMemo newMark) {

        // We add any points in our new mark to the set
        VoxelizedMark pxlMark = newMark.voxelized();

        ReadableTuple3i crnrPoint = pxlMark.boundingBox().cornerMin();

        RegionMembership rm = newMark.getRegionMap().membershipForIndex(regionID);
        byte flags = rm.flags();

        BoundedVoxels<ByteBuffer> voxels = pxlMark.voxels();
        Extent extent = voxels.extent();

        Point3i position = new Point3i();
        for (position.setZ(0); position.z() < extent.z(); position.incrementZ()) {

            ByteBuffer buffer = voxels.sliceBufferLocal(position.z());

            for (position.setY(0); position.y() < extent.y(); position.incrementY()) {
                for (position.setX(0); position.x() < extent.x(); position.incrementX()) {

                    byte membership = buffer.get(extent.offset(position.x(), position.y()));

                    if (!rm.isMemberFlag(membership, flags)) {
                        rmvPoint(position, crnrPoint);
                    }
                }
            }
        }
    }

    @Override
    public void exchange(
            MemoForIndex pxlMarkMemoList,
            VoxelizedMarkMemo oldMark,
            int indexOldMark,
            VoxelizedMarkMemo newMark) {

        addPointsInMark(pxlMarkMemoList, oldMark);
        removePointsInMark(newMark);
    }

    public void addPointsInMark(MemoForIndex marksExisting, VoxelizedMarkMemo markToAdd) {
        // We add any points in our new mark to the set, but only if there's not already a neighbor
        // covering them

        // So our first step is to identify any overlapping marks
        List<VoxelizedMarkMemo> neighbors = findNeighbors(marksExisting, markToAdd);

        VoxelizedMark pxlMark = markToAdd.voxelized();

        ReadableTuple3i crnrPoint = pxlMark.boundingBox().cornerMin();

        RegionMembership rm = markToAdd.getRegionMap().membershipForIndex(regionID);

        BoundedVoxels<ByteBuffer> voxels = pxlMark.voxels();
        Extent e = voxels.extent();

        BinaryValuesByte bvb = mask.binaryValues().createByte();

        Voxels<ByteBuffer> voxelsBinary = maskChannel.voxels().asByte();

        Point3i point = new Point3i();
        for (point.setZ(0); point.z() < e.z(); point.incrementZ()) {

            int zGlobal = crnrPoint.z() + point.z();

            addPointsForSlice(
                    point,
                    crnrPoint,
                    e,
                    voxels.sliceBufferLocal(point.z()),
                    voxelsBinary.sliceBuffer(zGlobal),
                    bvb,
                    zGlobal,
                    rm,
                    neighbors);
        }
    }

    private void addPointsForSlice( // NOSONAR
            Point3i crntExtentPoint,
            ReadableTuple3i crnrPoint,
            Extent extent,
            ByteBuffer buffer,
            ByteBuffer bbBinaryImage,
            BinaryValuesByte bvb,
            int zGlobal,
            RegionMembership rm,
            List<VoxelizedMarkMemo> neighbors) {
        byte flags = rm.flags();

        for (crntExtentPoint.setY(0);
                crntExtentPoint.y() < extent.y();
                crntExtentPoint.incrementY()) {
            int yGlobal = crnrPoint.y() + crntExtentPoint.y();

            for (crntExtentPoint.setX(0);
                    crntExtentPoint.x() < extent.x();
                    crntExtentPoint.incrementX()) {

                int xGlobal = crnrPoint.x() + crntExtentPoint.x();

                int globOffset = extent.offset(xGlobal, yGlobal);
                byte posCheck = buffer.get(extent.offset(crntExtentPoint.x(), crntExtentPoint.y()));
                if (rm.isMemberFlag(posCheck, flags)
                        && bbBinaryImage.get(globOffset) == bvb.getOnByte()) {

                    Point3d pointGlobal = new Point3d(xGlobal, yGlobal, zGlobal);

                    // Now we check to make sure the point isn't contained in any of its neighbors
                    if (!isPointInList(neighbors, pointGlobal)) {
                        setPoints.add(pointGlobal);
                    }
                }
            }
        }
    }

    private List<VoxelizedMarkMemo> findNeighbors(MemoForIndex all, VoxelizedMarkMemo source) {

        ArrayList<VoxelizedMarkMemo> list = new ArrayList<>();

        for (int i = 0; i < all.size(); i++) {

            VoxelizedMarkMemo pmm = all.getMemoForIndex(i);
            if (pmm != source && OverlapUtilities.overlapWith(source, pmm, regionID) > 0) {
                // We check if there's any overlap
                list.add(pmm);
            }
        }
        return list;
    }

    private static boolean isPointInList(List<VoxelizedMarkMemo> all, Point3d point) {

        for (VoxelizedMarkMemo memo : all) {

            RegionMembership rm =
                    memo.getRegionMap().membershipForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE);
            byte flags = rm.flags();

            byte membership = memo.getMark().isPointInside(point);
            if (rm.isMemberFlag(membership, flags)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void remove(MemoForIndex marksExisting, VoxelizedMarkMemo mark)
            throws UpdateMarkSetException {
        addPointsInMark(marksExisting, mark);
    }

    @Override
    public int size() {
        return setPoints.size();
    }

    private static <T> T sampleFromSet(
            RandomSet<T> set, RandomNumberGenerator randomNumberGenerator) {
        return set.get(randomNumberGenerator.sampleIntFromRange(set.size()));
    }
}
