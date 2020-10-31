package org.anchoranalysis.image.voxel.arithmetic;

import java.util.function.LongUnaryOperator;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class UnsignedIntHelper {

    private static final long MAXIMUM_VALUE = UnsignedIntVoxelType.MAX_VALUE;
    
    public static void calculateForEveryVoxel(UnsignedIntBuffer buffer, LongUnaryOperator operator) {
        while (buffer.hasRemaining()) {
            putClipped(buffer, operator.applyAsLong(buffer.getUnsigned()));
        }
    }
    public static void calculateForIndex(UnsignedIntBuffer buffer, int index, LongUnaryOperator operator) {
        putClippedAtIndex(buffer, operator.applyAsLong(buffer.getUnsigned(index)), index);
    }
    
    /** Put a (clipped via a long) double-value at previous buffer position. */
    private static void putClipped(UnsignedIntBuffer buffer, long valueToAssign) {
        putClippedAtIndex(buffer, valueToAssign, buffer.position() - 1);
    }
    
    /** Put a (clipped) double-value at previous buffer position */
    private static void putClippedAtIndex(UnsignedIntBuffer buffer, long valueToAssign, int index) {
        buffer.putUnsigned(index, clip(valueToAssign) );
    }
    
    private static long clip(long value) {
        if (value < 0) {
            return 0;
        }
        if (value > MAXIMUM_VALUE) {
            return MAXIMUM_VALUE;
        }
        return value;
    }
}
