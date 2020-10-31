package org.anchoranalysis.image.voxel.arithmetic;

import java.util.function.IntUnaryOperator;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedBufferAsInt;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class UnsignedBufferAsIntHelper {
    
    public static void calculateForIndex(UnsignedBufferAsInt buffer, int maximumValue, int index, IntUnaryOperator operator) {
        putClippedAtIndex(buffer, maximumValue, operator.applyAsInt(buffer.getUnsigned(index)), index);
    }
        
    public static void calculateForEveryVoxel(UnsignedBufferAsInt buffer, int maximumValue, IntUnaryOperator operator) {
        while (buffer.hasRemaining()) {
            int valueToAssign = operator.applyAsInt(buffer.getUnsigned());
            putClipped(buffer, maximumValue, valueToAssign);
        }
    }

    /** Put a (clipped) int-value at previous buffer position. */
    private static void putClipped(UnsignedBufferAsInt buffer, int maximumValue, int valueToAssign) {
        putClippedAtIndex(buffer, maximumValue, valueToAssign, buffer.position() - 1);
    }
    
    /** Put a (clipped) int-value at a particular index. */
    private static void putClippedAtIndex(UnsignedBufferAsInt buffer, int maximumValue, int valueToAssign, int index) {
        buffer.putUnsigned(index, clip(valueToAssign, maximumValue) );
    }

    private static int clip(int value, int maximumValue) {
        if (value < 0) {
            return 0;
        }
        if (value > maximumValue) {
            return maximumValue;
        }
        return value;
    }
}
