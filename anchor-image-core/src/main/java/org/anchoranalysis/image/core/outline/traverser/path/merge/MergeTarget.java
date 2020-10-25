/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.image.core.outline.traverser.path.merge;

import org.anchoranalysis.image.core.outline.traverser.path.ContiguousVoxelPath;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * A path and a location onto which a merge can occur, and the index of the vertex in the original
 * list
 *
 * @author Owen Feehan
 */
class MergeTarget {

    private ContiguousVoxelPath path;
    private int indexPath;
    private Point3i mergePoint;
    private int mergeIndex;

    public MergeTarget(ContiguousVoxelPath path, int indexPath, int mergeIndex) {
        super();
        this.path = path;
        this.indexPath = indexPath;
        this.mergeIndex = mergeIndex;
    }

    public ContiguousVoxelPath getPath() {
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
