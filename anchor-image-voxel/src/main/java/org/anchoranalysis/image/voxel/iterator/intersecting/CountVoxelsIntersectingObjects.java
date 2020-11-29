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
package org.anchoranalysis.image.voxel.iterator.intersecting;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.iterator.predicate.PredicateTwoBytes;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Like {@link CountVoxelsIntersectingBounded} but specifically counts areas of intersection between
 * two {@link ObjectMask}s.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CountVoxelsIntersectingObjects {

    /**
     * Determines whether there are any intersecting voxels on two object-masks.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * <p>The algorithm exits as soon as an intersecting voxel is encountered i.e. as early as
     * possible.
     *
     * @param object1 first-object
     * @param object2 second-object
     * @return true if at least one voxel exists that is <i>on</i> in both object-masks.
     */
    public static boolean hasIntersectingVoxels(ObjectMask object1, ObjectMask object2) {
        PredicateTwoBytes predicate = voxelInBothObjects(object1, object2);
        Optional<Point3i> firstPoint =
                IterateVoxelsIntersectingBounded.withTwoBuffersUntil(
                        object1.boundedVoxels(),
                        object2.boundedVoxels(),
                        predicate.deriveUnsignedBytePredicate());
        return firstPoint.isPresent();
    }

    /**
     * Counts the number of intersecting-voxels between two object-masks.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param object1 first-object
     * @param object2 second-object
     * @return number of <i>on</i>-voxels the two object-masks have in common.
     */
    public static int countIntersectingVoxels(ObjectMask object1, ObjectMask object2) {
        return CountVoxelsIntersectingBounded.countByte(
                object1.boundedVoxels(),
                object2.boundedVoxels(),
                voxelInBothObjects(object1, object2));
    }

    /** Is a voxel <i>on</i> in both object-masks? */
    private static PredicateTwoBytes voxelInBothObjects(ObjectMask object1, ObjectMask object2) {
        final byte byteOn1 = object1.binaryValuesByte().getOnByte();
        final byte byteOn2 = object2.binaryValuesByte().getOnByte();
        return (first, second) -> first == byteOn1 && second == byteOn2;
    }
}
