/* (C)2020 */
package org.anchoranalysis.image.bean.object;

import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.bean.ImageBean;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.ObjectCollection;

public abstract class ObjectFilter extends ImageBean<ObjectFilter> {

    /**
     * Filters an object-collection to remove objects according to a criteria
     *
     * @param objectsToFilter the objects to filter
     * @param dim image-dimensions
     * @param objectsRejected if set, any objects rejected by the filter can be added to this
     *     collection
     * @return a new object-collection containing only the objects that passed the filter
     * @throws OperationFailedException
     */
    public abstract ObjectCollection filter(
            ObjectCollection objectsToFilter,
            Optional<ImageDimensions> dim,
            Optional<ObjectCollection> objectsRejected)
            throws OperationFailedException;
}
