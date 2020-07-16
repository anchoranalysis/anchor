/* (C)2020 */
package org.anchoranalysis.image.voxel.neighborhood;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class EdgeAdderParameters {

    /**
     * Iff true, objects can only be neighbors if they have no intersecting voxels
     *
     * <p>Use case: Given a partition of objects, we don't need to check if objects-intersect as
     * it's not possible by definition (a partition). However, if we have objects that can
     * potentially overlap, we define them as 'neighbors' only if objects are adjacent, but don't
     * overlap. In this case, we need to check
     */
    private final boolean preventObjectIntersection;

    /** iff TRUE each combination of neighbors is tested only once, otherwise twice */
    private boolean testBothDirections = false;

    /** iff TRUE uses a big-neighborhood (8 or 26 connectivity) for dilation */
    private boolean bigNeighborhood = false;
}
