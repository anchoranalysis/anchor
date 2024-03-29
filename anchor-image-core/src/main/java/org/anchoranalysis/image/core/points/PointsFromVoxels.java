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

package org.anchoranalysis.image.core.points;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsEqualTo;
import org.anchoranalysis.spatial.point.Comparator3i;
import org.anchoranalysis.spatial.point.Point2i;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.PointConverter;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Converts binary-voxels into points
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PointsFromVoxels {

    private static final Point3i ZERO_SHIFT = new Point3i(0, 0, 0);

    /**
     * Creates a list of 2 dimensional integer points for each voxel
     *
     * @param voxels binary-voxels, in each <i>on</i> voxel represents a point
     * @return a newly created list
     * @throws CreateException if the voxels have three dimensions
     */
    public static List<Point2i> listFrom2i(BinaryVoxels<UnsignedByteBuffer> voxels)
            throws CreateException {
        return listFrom2i(voxels, ZERO_SHIFT);
    }

    /**
     * Creates a list of 3 dimensional integer points for each voxel
     *
     * @param voxels binary-voxels, in each <i>on</i> voxel represents a point
     * @return a newly created list
     */
    public static List<Point3i> listFrom3i(BinaryVoxels<UnsignedByteBuffer> voxels) {
        return listFrom3i(voxels, ZERO_SHIFT);
    }

    /**
     * Creates a list of 2 dimensional integer points for each voxel
     *
     * @param voxels binary-voxels, in each <i>on</i> voxel represents a point
     * @param shift adds this shift to each point
     * @return a newly created list
     * @throws CreateException if the voxels have three dimensions
     */
    public static List<Point2i> listFrom2i(
            BinaryVoxels<UnsignedByteBuffer> voxels, ReadableTuple3i shift) throws CreateException {

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
     * @param voxels binary-voxels, in each <i>on</i> voxel represents a point
     * @param shift adds this shift to each point
     * @return a newly created list
     */
    public static List<Point3i> listFrom3i(
            BinaryVoxels<UnsignedByteBuffer> voxels, ReadableTuple3i shift) {
        List<Point3i> points = new ArrayList<>();
        PointsFromVoxels.consumePoints3i(voxels, shift, points::add);
        return points;
    }

    /**
     * Creates a {@link TreeSet} of 3 dimensional integer points for each voxel with an added shift
     *
     * @param voxels binary-voxels, in each <i>on</i> voxel represents a point
     * @param shift adds this shift to each point
     * @return a newly created list
     */
    public static SortedSet<Point3i> setFrom3i(
            BinaryVoxels<UnsignedByteBuffer> voxels, ReadableTuple3i shift) {
        SortedSet<Point3i> points = new TreeSet<>(new Comparator3i<>());
        PointsFromVoxels.consumePoints3i(voxels, shift, points::add);
        return points;
    }

    /**
     * Creates a list of 3 dimensional double points for each voxel with an added shift
     *
     * @param voxels binary-voxels, in each <i>on</i> voxel represents a point
     * @param shift adds this shift to each point
     * @return a newly created list
     */
    public static List<Point3d> listFrom3d(
            BinaryVoxels<UnsignedByteBuffer> voxels, ReadableTuple3i shift) {
        List<Point3d> points = new ArrayList<>();
        PointsFromVoxels.consumePoints3d(voxels, PointConverter.doubleFromInt(shift), points::add);
        return points;
    }

    /**
     * Consumes a two dimensional integer point for each <i>on</i> voxel (with a possible shift)
     *
     * <p>The z-dimension is ignored for each point.
     *
     * <p>No check occurs that a stack is 2 dimensional, so it can be called on a three dimensional
     * stack possibly producing multiple identical points.
     *
     * @param voxels binary-voxels where each <i>on</i> voxel produces a point
     * @param shift adds this shift to each point
     * @param consumer called for each point
     */
    public static void consumePoints2i(
            BinaryVoxels<UnsignedByteBuffer> voxels,
            ReadableTuple3i shift,
            Consumer<Point2i> consumer) {

        BinaryValuesByte binaryValues = voxels.binaryValues().asByte();
        IterateVoxelsEqualTo.equalToPrimitiveSlice(
                voxels.voxels(),
                0,
                binaryValues.getOn(),
                (x, y, z) -> consumer.accept(new Point2i(shift.x() + x, shift.y() + y)));
    }

    /**
     * Consumes a three dimensional integer point for each <i>on</i> voxel (with a possible shift)
     *
     * @param voxels binary-voxels where each <i>on</i> voxel produces a point
     * @param shift adds this shift to each point
     * @param consumer called for each point
     */
    private static void consumePoints3i(
            BinaryVoxels<UnsignedByteBuffer> voxels,
            ReadableTuple3i shift,
            Consumer<Point3i> consumer) {
        BinaryValuesByte binaryValues = voxels.binaryValues().asByte();
        IterateVoxelsEqualTo.equalToPrimitive(
                voxels.voxels(),
                binaryValues.getOn(),
                (x, y, z) -> consumer.accept(Point3i.immutableAdd(shift, x, y, z)));
    }

    /**
     * Consumes a three dimensional double point for each <i>on</i> voxel (with a possible shift)
     *
     * @param voxels binary-voxels where each <i>on</i> voxel produces a point
     * @param add adds this shift to each point
     * @param consumer called for each point
     */
    private static void consumePoints3d(
            BinaryVoxels<UnsignedByteBuffer> voxels, Point3d add, Consumer<Point3d> consumer) {
        BinaryValuesByte binaryValues = voxels.binaryValues().asByte();
        IterateVoxelsEqualTo.equalToPrimitive(
                voxels.voxels(),
                binaryValues.getOn(),
                (x, y, z) -> consumer.accept(Point3d.immutableAdd(add, x, y, z)));
    }
}
