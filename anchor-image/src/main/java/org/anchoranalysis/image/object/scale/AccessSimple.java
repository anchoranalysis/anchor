package org.anchoranalysis.image.object.scale;

import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Provides access to the simple-case of a list of {@link ObjectMask}s.
 * 
 * @author Owen Feehan
 *
 */
class AccessSimple implements AccessObjectMask<ObjectMask> {

    @Override
    public ObjectMask objectFor(ObjectMask element) {
        return element;
    }

    @Override
    public ObjectMask shiftBy(ObjectMask element, ReadableTuple3i quantity) {
        return element.shiftBy(quantity);
    }

    @Override
    public ObjectMask createFrom(int index, ObjectMask object) {
        return object;
    }

    @Override
    public ObjectMask clipTo(ObjectMask element, Extent extent) {
        return element.clipTo(extent);
    }

}
