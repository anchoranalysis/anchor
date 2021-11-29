package org.anchoranalysis.image.core.object.scale.method;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Determines which instance of {@link ObjectScalingMethod} to apply.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectScalingMethodFactory {

    /**
     * Scale objects collectively, so as to preserve a tight border between objects. But objects
     * must be guaranteed not to overlap.
     */
    private static final ObjectScalingMethod COLLECTIVELY = new ScaleObjectsCollectively();

    /**
     * Scale objects independently, without preserving a tight border between objects. But objects
     * are allowed to overlap.
     */
    private static final ObjectScalingMethod INDEPENDENTLY = new ScaleObjectsIndependently();

    /**
     * Determines which method to use, given circumstances.
     *
     * @param overlappingObjects whether objects-to-scaled may overlap with each other.
     * @return an appropriate scaling method for the circumstance.
     */
    public static ObjectScalingMethod of(boolean overlappingObjects) {
        if (overlappingObjects) {
            return INDEPENDENTLY;
        } else {
            return COLLECTIVELY;
        }
    }
}
