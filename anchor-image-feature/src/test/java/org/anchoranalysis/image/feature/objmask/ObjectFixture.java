/* (C)2020 */
package org.anchoranalysis.image.feature.objmask;

import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Creates some basic objects for tests.
 *
 * <p>Currently unused, but keep as it will likely be useful in the future
 */
public class ObjectFixture {

    public static ObjectMask createSmallCornerObj() {
        return new ObjectMask(new BoundingBox(new Extent(2, 3, 1)));
    }
}
