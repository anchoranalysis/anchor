/* (C)2020 */
package org.anchoranalysis.image.interpolator;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

/** Doesn't do any interpolation, just copies values */
public class InterpolatorNone implements Interpolator {

    @Override
    public VoxelBuffer<ByteBuffer> interpolateByte(
            VoxelBuffer<ByteBuffer> src, VoxelBuffer<ByteBuffer> dest, Extent eSrc, Extent eDest) {

        copyByte(src.buffer(), dest.buffer(), eSrc, eDest);
        return dest;
    }

    @Override
    public VoxelBuffer<ShortBuffer> interpolateShort(
            VoxelBuffer<ShortBuffer> src,
            VoxelBuffer<ShortBuffer> dest,
            Extent eSrc,
            Extent eDest) {
        copyShort(src.buffer(), dest.buffer(), eSrc, eDest);
        return dest;
    }

    private static void copyByte(ByteBuffer bbIn, ByteBuffer bbOut, Extent eIn, Extent eOut) {

        double xScale = intDiv(eIn.getX(), eOut.getX());
        double yScale = intDiv(eIn.getY(), eOut.getY());

        // We loop through every pixel in the output buffer
        for (int y = 0; y < eOut.getY(); y++) {
            for (int x = 0; x < eOut.getX(); x++) {

                int xOrig = intMin(xScale * x, eIn.getX() - 1);
                int yOrig = intMin(yScale * y, eIn.getY() - 1);

                byte orig = bbIn.get(eIn.offset(xOrig, yOrig));
                bbOut.put(orig);
            }
        }
    }

    private static void copyShort(ShortBuffer bbIn, ShortBuffer bbOut, Extent eIn, Extent eOut) {

        double xScale = intDiv(eIn.getX(), eOut.getX());
        double yScale = intDiv(eIn.getY(), eOut.getY());

        // We loop through every pixel in the output buffer
        for (int y = 0; y < eOut.getY(); y++) {
            for (int x = 0; x < eOut.getX(); x++) {

                int xOrig = intMin(xScale * x, eIn.getX() - 1);
                int yOrig = intMin(yScale * y, eIn.getY() - 1);

                short orig = bbIn.get(eIn.offset(xOrig, yOrig));
                bbOut.put(orig);
            }
        }
    }

    private static double intDiv(int num, int dem) {
        return ((double) num) / dem;
    }

    private static int intMin(double val1, int val2) {
        return (int) Math.min(Math.round(val1), val2);
    }

    @Override
    public boolean isNewValuesPossible() {
        return false;
    }
}
