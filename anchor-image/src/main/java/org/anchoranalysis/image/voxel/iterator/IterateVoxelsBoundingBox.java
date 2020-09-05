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

package org.anchoranalysis.image.voxel.iterator;

import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.iterator.process.ProcessBufferUnary;
import org.anchoranalysis.image.voxel.iterator.process.ProcessPoint;

/**
 * Utilities for iterating over the subset of image voxels within a bounding-box.
 *
 * <p>The utilities operate on one or more {@link Voxels}. A processor is called on each selected voxel.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IterateVoxelsBoundingBox {

    /**
     * Iterate over each voxel in a bounding-box
     *
     * @param box the box that is used as a condition on what voxels to iterate i.e. only voxels
     *     within these bounds
     * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
     */
    public static void withPoint(BoundingBox box, ProcessPoint process) {

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calculateCornerMax();

        Point3i point = new Point3i();

        for (point.setZ(cornerMin.z()); point.z() <= cornerMax.z(); point.incrementZ()) {

            process.notifyChangeSlice(point.z());

            for (point.setY(cornerMin.y()); point.y() <= cornerMax.y(); point.incrementY()) {

                process.notifyChangeY(point.y());

                for (point.setX(cornerMin.x()); point.x() <= cornerMax.x(); point.incrementX()) {
                    process.process(point);
                }
            }
        }
    }

    /**
     * Iterate over each voxel in a bounding-box that matches a predicate
     *
     * @param box the box that is used as a condition on what voxels to iterate i.e. only voxels
     *     within these bounds
     * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
     */
    public static void withMatchingPoints(
            BoundingBox box, Predicate<Point3i> predicate, ProcessPoint process) {

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calculateCornerMax();

        Point3i point = new Point3i();

        for (point.setZ(cornerMin.z()); point.z() <= cornerMax.z(); point.incrementZ()) {

            process.notifyChangeSlice(point.z());

            for (point.setY(cornerMin.y()); point.y() <= cornerMax.y(); point.incrementY()) {

                process.notifyChangeY(point.y());

                for (point.setX(cornerMin.x()); point.x() <= cornerMax.x(); point.incrementX()) {
                    if (predicate.test(point)) {
                        process.process(point);
                    }
                }
            }
        }
    }

    /**
     * Iterate over each voxel in a bounding-box - with <b>one</b> associated buffer for each slice
     *
     * @param voxels voxels in which which {@link BoundingBox} refers to a subregion.
     * @param box the box that is used as a condition on what voxels to iterate i.e. only voxels
     *     within these bounds
     * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <T> void withBuffer(
            BoundingBox box, Voxels<T> voxels, ProcessBufferUnary<T> process) {
        withPoint(box, new RetrieveBufferForSlice<>(voxels, process));
    }
}
