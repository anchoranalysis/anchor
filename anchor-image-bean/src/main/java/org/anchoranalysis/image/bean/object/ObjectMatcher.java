/* (C)2020 */
package org.anchoranalysis.image.bean.object;

import java.util.List;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.bean.ImageBean;
import org.anchoranalysis.image.object.MatchedObject;
import org.anchoranalysis.image.object.ObjectCollection;

/**
 * Matches an object with other objects.
 *
 * @author Owen Feehan
 */
public abstract class ObjectMatcher extends ImageBean<ObjectMatcher> {

    /**
     * Finds matches for objects
     *
     * @param sourceObjects a collection of objects, each of whom is searched for matches
     * @return a list of each object from @code{sourceObjects} with its matches (element order
     *     remains unchanged)
     * @throws OperationFailedException
     */
    public abstract List<MatchedObject> findMatch(ObjectCollection sourceObjects)
            throws OperationFailedException;
}
