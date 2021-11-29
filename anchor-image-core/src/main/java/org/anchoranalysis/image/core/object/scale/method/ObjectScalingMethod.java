package org.anchoranalysis.image.core.object.scale.method;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.object.scale.AccessObjectMask;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * A way to scale a group of {@link ObjectMask}s, scaling each object independently or collectively.
 *
 * @author Owen Feehan
 */
public interface ObjectScalingMethod {

    /**
     * Applies a scaling factor to elements, where each element is associate with a unique {@link
     * ObjectMask}.
     *
     * @param <T> the element-type, which must have a sensible {@link Object#hashCode} and {@link
     *     Object#equals} implementation.
     * @param elements the collection of <i>input</i> elements, whose associated {@link ObjectMask}s
     *     will be scaled.
     * @param scaleFactor how much to scale by.
     * @param access means of retrieving the {@link ObjectMask} that is associated with {@code T}
     *     and to create new derived elements.
     * @return a map from input-element (unscaled) to the corresponding output-element (scaled).
     * @throws OperationFailedException if any aspect of the parameterization is unsupported by the
     *     method.
     */
    default <T> Map<T, T> scaleElements(
            List<T> elements, ScaleFactor scaleFactor, AccessObjectMask<T> access)
            throws OperationFailedException {
        return scaleElements(elements, scaleFactor, Optional.empty(), Optional.empty(), access);
    }

    /**
     * Applies a scaling factor to elements, where each element is associate with a unique {@link
     * ObjectMask}.
     *
     * @param <T> the element-type, which must have a sensible {@link Object#hashCode} and {@link
     *     Object#equals} implementation.
     * @param elements the collection of <i>input</i> elements, whose associated {@link ObjectMask}s
     *     will be scaled.
     * @param scaleFactor how much to scale by.
     * @param preOperation an optional operation applied to an element <b>before</b> scaling.
     * @param postOperation an optional operation applied to an element <b>after</b> scaling.
     * @param access means of retrieving the {@link ObjectMask} that is associated with {@code T}
     *     and to create new derived elements.
     * @return a map from input-element (unscaled) to the corresponding output-element (scaled).
     * @throws OperationFailedException if any aspect of the parameterization is unsupported by the
     *     method.
     */
    <T> Map<T, T> scaleElements(
            List<T> elements,
            ScaleFactor scaleFactor,
            Optional<UnaryOperator<T>> preOperation,
            Optional<UnaryOperator<T>> postOperation,
            AccessObjectMask<T> access)
            throws OperationFailedException;
}
