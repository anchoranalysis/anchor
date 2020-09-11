package org.anchoranalysis.image.object.scale;

import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Provides functions to provide access and creation to/from the object-mask representation of a generic type.
 * 
 * <p>The purpose is to allow both {@link ObjectMask} and other types that may contain a {@link ObjectMask}
 * to be scaled, with generic functions providing access and creation to/from the object-mask representation.
 * 
 * @author Owen Feehan
 *
 * @param <T> element-type, either an object-mask or has an object-mask representation 
 */
public interface AccessObjectMask<T> {
        
    /**
     * An object-mask for a given element.
     * 
     * <p>This operation is assumed to involve negligible computational cost.
     * 
     * @param element the element
     * @return the object-mask
     */
    ObjectMask objectFor(T element);
    
    /**
     * Positionally-shifts an element by a given quantity in the positive direction.
     * 
     * @param element the element to shift by
     * @param quantity the quantity to shift by
     * @return a newly created element based on {@code element} but positionally-shifted. 
     */
    T shiftBy(T element, ReadableTuple3i quantity);
    
    
    /**
     * Ensures the element lies within a certain extent.
     * 
     * @param extent the extent to clip to
     * @return either a newly created element or the existing element (if no change needs to occur)
     */
    T clipTo(T element, Extent extent);
    
    /**
     * Creates an element of type {@code T} from an object-representation and index.
     * 
     * @param index the index of the object-representation in terms of the original list
     * @param object an object-representation corresponding to this index
     * @return a newly created element corresponding to the object-representation.
     */
    T createFrom(int index, ObjectMask object);
    
    
    /**
     * A bounding-box for a given element.
     * 
     * <p>This operation is assumed to involve negligible computational cost.
     * 
     * @param element the element
     * @return the bounding box.
     */
    default BoundingBox boundingBoxFor(T element) {
        return objectFor(element).boundingBox();
    }
}
