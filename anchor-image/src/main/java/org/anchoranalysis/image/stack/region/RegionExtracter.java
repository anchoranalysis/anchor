/* (C)2020 */
package org.anchoranalysis.image.stack.region;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.stack.DisplayStack;

// Extracts regions from a DisplayStack for presentation to the user
public interface RegionExtracter {

    DisplayStack extractRegionFrom(BoundingBox bbox, double zoomFactor)
            throws OperationFailedException;
}
