/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.points;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Comparator3i;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxels;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsByte;

/**
 * Converts binary-voxels into points
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointsFromVoxels {

    private static final Point3i ZERO_SHIFT = new Point3i(0, 0, 0);

    /**
     * Creates a list of 2 dimensional integer points for each voxel
     *
     * @param voxels binary-voxels, in each ON voxel represents a point
     * @return a newly created list
     * @throws CreateException if the voxels have three dimensions
     */
    public static List<Point2i> listFrom2i(BinaryVoxels<ByteBuffer> voxels) throws CreateException {
        return listFrom2i(voxels, ZERO_SHIFT);
    }

    /**
     * Creates a list of 3 dimensional integer points for each voxel
     *
     * @param voxels binary-voxels, in each ON voxel represents a point
     * @return a newly created list
     */
    public static List<Point3i> listFrom3i(BinaryVoxels<ByteBuffer> voxels) {
        return listFrom3i(voxels, ZERO_SHIFT);
    }

    /**
     * Creates a list of 2 dimensional integer points for each voxel
     *
     * @param voxels binary-voxels, in each ON voxel represents a point
     * @param shift adds this shift to each point
     * @return a newly created list
     * @throws CreateException if the voxels have three dimensions
     */
    public static List<Point2i> listFrom2i(BinaryVoxels<ByteBuffer> voxels, ReadableTuple3i shift)
            throws CreateException {

        List<Point2i> out = new ArrayList<>();

        if (voxels.extent().z() > 1) {
            throw new CreateException("Only works in 2D. No z-stack alllowed");
        }

        consumePoints2i(voxels, shift, out::add);

        return out;
    }

    /**
     * Creates a list of 3 dimensional integer points for each voxel with an added shift
     *
     * @param voxels binary-voxels, in each ON voxel represents a point
     * @param shift adds this shift to each point
     * @return a newly created list
     */
    public static List<Point3i> listFrom3i(BinaryVoxels<ByteBuffer> voxels, ReadableTuple3i shift) {
        List<Point3i> points = new ArrayList<>();
        PointsFromVoxels.consumePoints3i(voxels, shift, points::add);
        return points;
    }

    /**
     * Creates a {@link TreeSet} of 3 dimensional integer points for each voxel with an added shift
     *
     * @param voxels binary-voxels, in each ON voxel represents a point
     * @param shift adds this shift to each point
     * @return a newly created list
     */
    public static SortedSet<Point3i> setFrom3i(
            BinaryVoxels<ByteBuffer> voxels, ReadableTuple3i shift) {
        SortedSet<Point3i> points = new TreeSet<>(new Comparator3i<>());
        PointsFromVoxels.consumePoints3i(voxels, shift, points::add);
        return points;
    }

    /**
     * Creates a list of 3 dimensional double points for each voxel with an added shift
     *
     * @param voxels binary-voxels, in each ON voxel represents a point
     * @param shift adds this shift to each point
     * @return a newly created list
     */
    public static List<Point3d> listFrom3d(BinaryVoxels<ByteBuffer> voxels, ReadableTuple3i shift) {
        List<Point3d> points = new ArrayList<>();
        PointsFromVoxels.consumePoints3d(voxels, PointConverter.doubleFromInt(shift), points::add);
        return points;
    }

    /**
     * Consumes a two dimensional integer point for each ON voxel (with a possible shift)
     *
     * <p>The z-dimension is ignored for each point.
     *
     * <p>No check occurs that a stack is 2 dimensional, so it can be called on a three dimensional
     * stack possibly producing multiple identical points.
     *
     * @param voxels binary-voxels where each ON voxel produces a point
     * @param shift adds this shift to each point
     * @param consumer called for each point
     */
    static void consumePoints2i(
            BinaryVoxels<ByteBuffer> voxels, ReadableTuple3i shift, Consumer<Point2i> consumer) {

        BinaryValuesByte bvb = voxels.binaryValues().createByte();
        IterateVoxelsByte.iterateEqualValuesSlice(
                voxels.voxels(),
                0,
                bvb.getOnByte(),
                (x, y, z) -> consumer.accept(new Point2i(shift.x() + x, shift.y() + y)));
    }

    /**
     * Consumes a three dimensional integer point for each ON voxel (with a possible shift)
     *
     * @param voxels binary-voxels where each ON voxel produces a point
     * @param shift adds this shift to each point
     * @param consumer called for each point
     */
    private static void consumePoints3i(
            BinaryVoxels<ByteBuffer> voxels, ReadableTuple3i shift, Consumer<Point3i> consumer) {
        BinaryValuesByte bvb = voxels.binaryValues().createByte();
        IterateVoxelsByte.iterateEqualValues(
                voxels.voxels(),
                bvb.getOnByte(),
                (x, y, z) -> consumer.accept(Point3i.immutableAdd(shift, x, y, z)));
    }

    /**
     * Consumes a three dimensional double point for each ON voxel (with a possible shift)
     *
     * @param voxels binary-voxels where each ON voxel produces a point
     * @param add adds this shift to each point
     * @param consumer called for each point
     */
    private static void consumePoints3d(
            BinaryVoxels<ByteBuffer> voxels, Point3d add, Consumer<Point3d> consumer) {
        BinaryValuesByte bvb = voxels.binaryValues().createByte();
        IterateVoxelsByte.iterateEqualValues(
                voxels.voxels(),
                bvb.getOnByte(),
                (x, y, z) -> consumer.accept(Point3d.immutableAdd(add, x, y, z)));
    }
}
