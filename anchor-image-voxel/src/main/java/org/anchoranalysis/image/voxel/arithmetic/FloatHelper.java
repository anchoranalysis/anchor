package org.anchoranalysis.image.voxel.arithmetic;

import java.nio.FloatBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class FloatHelper {
    
    @FunctionalInterface
    public interface FloatUnaryOperator {
        public float applyAsFloat(float value);
    }
    
    public static void calculateForEveryVoxel(FloatBuffer buffer, FloatUnaryOperator operator) {
        while (buffer.hasRemaining()) {
            buffer.put(operator.applyAsFloat(buffer.get()));
        }
    }
    public static void calculateForIndex(FloatBuffer buffer, int index, FloatUnaryOperator operator) {
        buffer.put(index, operator.applyAsFloat(buffer.get(index)));
    }
}
