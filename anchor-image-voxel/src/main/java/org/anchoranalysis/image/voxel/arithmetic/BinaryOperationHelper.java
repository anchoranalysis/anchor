package org.anchoranalysis.image.voxel.arithmetic;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class BinaryOperationHelper {
    
    public static int multiplyByInt(int value, double factor) {
        return (int) Math.round(value * factor);
    }
    
    public static long multiplyByLong(long value, double factor) {
        return Math.round(value * factor);
    }
    
    public static float multiplyByFloat(float value, double factor) {
        return (float) Math.round(value * factor);
    }
}
