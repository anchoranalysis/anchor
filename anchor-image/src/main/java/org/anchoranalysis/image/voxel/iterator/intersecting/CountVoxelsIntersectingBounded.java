package org.anchoranalysis.image.voxel.iterator.intersecting;

import org.anchoranalysis.core.arithmetic.Counter;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsBoundingBox;
import org.anchoranalysis.image.voxel.iterator.predicate.PredicateTwoBytes;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Like {@link IterateVoxelsBoundingBox} but counts voxels matching a predicate rather than iterating.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class CountVoxelsIntersectingBounded {
    
    /**
     * Counts all voxels in the intersection of two bounded-voxels of type {@link BoundedVoxels} that match a predicate.
     * 
     * @param voxels1 the first bounded-voxels
     * @param voxels2 the second bounded-voxels
     * @param predicate determines if a particular voxel should be counted or not?
     * @return the number of voxels that match the predicate.
     */
    public static int countByte(
            BoundedVoxels<UnsignedByteBuffer> voxels1,
            BoundedVoxels<UnsignedByteBuffer> voxels2,
            PredicateTwoBytes predicate
    ) {
        Counter counter = new Counter();
        IterateVoxelsIntersectingBounded.withTwoBuffers(voxels1, voxels2, (point, buffer1, buffer2, offset1, offset2) -> {
            if (predicate.test(buffer1.getRaw(offset1), buffer2.getRaw(offset2))) {
                counter.increment();
            }
        });
        return counter.getCount();
    }
    
    /**
     * Counts all voxels intersection of two bounded-voxels of type {@link BoundedVoxels} but only voxels that lie on an object-mask and match a predicate.
     * 
     * @param maskGlobal a mask defined on the entire global space, and all matching voxels must have an <i>on</i> value in this mask, in addition to being part of the intersection of {@code voxels1} and {@code voxels2}.
     * @param onMaskGlobal the <i>on</i> value in {@code maskGlobal}. 
     * @param voxels1 the first bounded-voxels
     * @param voxels2 the second bounded-voxels
     * @param predicate determines if a particular voxel should be counted or not?
     * @return the number of voxels that match the predicate.
     */
    public static int countByteMasked(
            Voxels<UnsignedByteBuffer> maskGlobal,
            byte onMaskGlobal,
            BoundedVoxels<UnsignedByteBuffer> voxels1,
            BoundedVoxels<UnsignedByteBuffer> voxels2,
            PredicateTwoBytes predicate
    ) {
        Counter counter = new Counter();
        IterateVoxelsIntersectingBounded.withTwoBuffers(maskGlobal, onMaskGlobal, voxels1, voxels2, (point, buffer1, buffer2, offset1, offset2) -> {
            if (predicate.test(buffer1.getRaw(offset1), buffer2.getRaw(offset2))) {
                counter.increment();
            }
        });
        return counter.getCount();
    }
}
