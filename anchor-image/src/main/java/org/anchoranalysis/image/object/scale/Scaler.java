package org.anchoranalysis.image.object.scale;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.scale.ScaleFactor;
import lombok.AccessLevel;

import lombok.NoArgsConstructor;

/**
 * Scales object-masks (or other more generic elements) collectively to avoid undesired overlap.
 * 
 * <p>If each object-mask is scaled independently, touching but non-overlapping objects can
 * become overlapping when scaled. This provides a <i>collective</i> scaling procedure
 * that avoids this using nearest-neighbour interpolation.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class Scaler {
    
    private static final AccessObjectMask<ObjectMask> ACCESS_OBJECTS = new AccessSimple();
    
    /**
     * Scales every object-mask in a collection, ensuring the results remain inside a particular region.
     *
     * @param objects objects to scale
     * @param factor scaling-factor
     * @param clipTo clips any objects after scaling to make sure they fit inside this extent
     * @return a new collection with scaled object-masks
     * @throws OperationFailedException
     */
    public static ScaledElements<ObjectMask> scaleObjects(ObjectCollection objects, ScaleFactor factor, Extent clipTo) throws OperationFailedException {
        try {
            return new ScaledElements<>(
                    objects.asList(), factor, Optional.empty(), Optional.of(object -> object.clipTo(clipTo)), ACCESS_OBJECTS);
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }
    
    /**
     * Scales every object-mask in a collection, allowing for additional manipulation before and after scaling.
     *
     * @param objects objects to scale
     * @param factor scaling-factor
     * @param preOperation applied to each-object before it is scaled (e.g. flattening)
     * @param postOperation applied to each-object after it is scaled (e.g. clipping to an extent)
     * @return a new collection with scaled object-masks
     * @throws OperationFailedException
     */
    public static ScaledElements<ObjectMask> scaleObjects(
            ObjectCollection objects,
            ScaleFactor factor,
            Optional<UnaryOperator<ObjectMask>> preOperation,
            Optional<UnaryOperator<ObjectMask>> postOperation)
            throws OperationFailedException {
        try {
            return new ScaledElements<>(objects.asList(), factor, preOperation, postOperation, ACCESS_OBJECTS);
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }
    
    /**
     * Scales every element in a list collectively, ensuring the results remain inside a particular region.
     *
     * <p>This is similar to {@link #scaleObjects(ObjectCollection, ScaleFactor, Extent)} but accepts a parameterized type, rather than {@link ObjectMask}.
     * 
     * @param elements objects to scale
     * @param factor scaling-factor
     * @param clipTo clips any objects after scaling to make sure they fit inside this extent
     * @return a new collection with scaled elements
     * @throws OperationFailedException
     */
    public static <T> ScaledElements<T> scaleElements(List<T> elements, ScaleFactor factor, Extent clipTo, AccessObjectMask<T> access) throws OperationFailedException {
        try {
            return new ScaledElements<>(
                    elements, factor, Optional.empty(), Optional.of(object -> access.clipTo(object, clipTo)), access);
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }
}