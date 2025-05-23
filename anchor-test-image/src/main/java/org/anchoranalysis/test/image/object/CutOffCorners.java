/*-
 * #%L
 * anchor-test-image
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

package org.anchoranalysis.test.image.object;

import org.anchoranalysis.spatial.box.Extent;

/** The bounding box is filled apart from cuboids cut out of the corners */
public class CutOffCorners implements VoxelPattern {

    private int edgeXY;
    private int edgeZ;

    // The right-most pixels border, before we start chopping off the triangle
    private Extent rightBorder;

    /**
     * Constructs a CutOffCorners pattern.
     *
     * @param edgeXY The size of the edge to cut off in the XY plane.
     * @param edgeZ The size of the edge to cut off in the Z direction.
     * @param extent The total extent of the bounding box.
     */
    public CutOffCorners(int edgeXY, int edgeZ, Extent extent) {
        this.edgeXY = edgeXY;
        this.edgeZ = edgeZ;

        this.rightBorder =
                new Extent(
                        extent.x() - edgeXY - 1, extent.y() - edgeXY - 1, extent.z() - edgeZ - 1);
    }

    // Predicate on whether a pixel is included or not - triangle pattern at the edges
    @Override
    public boolean isPixelOn(int x, int y, int z) {
        if (x < edgeXY) {
            return false;
        }
        if (x > rightBorder.x()) {
            return false;
        }
        if (y < edgeXY) {
            return false;
        }
        if (y > rightBorder.y()) {
            return false;
        }
        if (z < edgeZ) {
            return false;
        }
        return z <= rightBorder.z();
    }
}
