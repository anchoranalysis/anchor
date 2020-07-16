/* (C)2020 */
package org.anchoranalysis.image.outline.traverser;

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;

/**
 * Condition where we visit the first pixel we are allowed to visit, and then prevent any more
 * visits
 *
 * <p>The first time it visits a pixel, then it toggles alreadyFoundSingleDirection
 */
@RequiredArgsConstructor
class VisitOneDirectionOnly implements ConsiderVisit {

    private final ConsiderVisit visitCondition;

    // Tracks if we've already found a direction to traverse alreadyFoundSingleDirection
    private boolean alreadyFoundSingleDirection = false;

    @Override
    public boolean considerVisit(Point3i point, int distance) {
        boolean canVisit = visitCondition.considerVisit(point, distance);
        if (canVisit) {
            if (!alreadyFoundSingleDirection) {
                alreadyFoundSingleDirection = true;
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }
}
