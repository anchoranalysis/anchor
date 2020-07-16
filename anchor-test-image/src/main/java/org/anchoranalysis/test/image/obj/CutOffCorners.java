/* (C)2020 */
package org.anchoranalysis.test.image.obj;

import org.anchoranalysis.image.extent.Extent;

/** The bounding box is filled apart from cuboids cut out of the corners */
public class CutOffCorners implements VoxelPattern {

    private int edgeXY;
    private int edgeZ;

    // The right-most pixels border, before we start chopping off the triangle
    private Extent rightBorder;

    public CutOffCorners(int edgeXY, int edgeZ, Extent extent) {
        this.edgeXY = edgeXY;
        this.edgeZ = edgeZ;

        this.rightBorder =
                new Extent(
                        extent.getX() - edgeXY - 1,
                        extent.getY() - edgeXY - 1,
                        extent.getZ() - edgeZ - 1);
    }

    // Predicate on whether a pixel is included or not - triangle pattern at the edges
    @Override
    public boolean isPixelOn(int x, int y, int z) {
        if (x < edgeXY) {
            return false;
        }
        if (x > rightBorder.getX()) {
            return false;
        }
        if (y < edgeXY) {
            return false;
        }
        if (y > rightBorder.getY()) {
            return false;
        }
        if (z < edgeZ) {
            return false;
        }
        return z <= rightBorder.getZ();
    }
}
