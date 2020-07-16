/* (C)2020 */
package org.anchoranalysis.image.outline.traverser;

import org.anchoranalysis.core.geometry.Point3i;

@FunctionalInterface
public interface ConsiderVisit {
    public boolean considerVisit(Point3i point, int distance);
}
