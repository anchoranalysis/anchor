/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.visitedpixels.combine;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;

/**
 * A path and a location onto which a merge can occur, and the index of the vertice in the original
 * list
 *
 * @author Owen Feehan
 */
class MergeTarget {

    private ContiguousPixelPath path;
    private int indexPath;
    private Point3i mergePoint;
    private int mergeIndex;

    public MergeTarget(ContiguousPixelPath path, int indexPath, int mergeIndex) {
        super();
        this.path = path;
        this.indexPath = indexPath;
        this.mergeIndex = mergeIndex;
    }

    public ContiguousPixelPath getPath() {
        return path;
    }

    public int getIndexPath() {
        return indexPath;
    }

    public Point3i mergePoint() {
        return path.get(mergeIndex);
    }

    @Override
    public String toString() {
        return String.format("path=%s point=%s indexPath=%d%n", path, mergePoint, indexPath);
    }
}
