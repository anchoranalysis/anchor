/* (C)2020 */
package org.anchoranalysis.image.voxel.kernel.outline;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;

// Keeps any on pixel that touches an off pixel, off otheriwse
public class OutlineKernel3 extends OutlineKernel3Base {

    public OutlineKernel3(BinaryValuesByte bv, boolean outsideAtThreshold, boolean useZ) {
        this(bv, outsideAtThreshold, useZ, false);
    }

    public OutlineKernel3(
            BinaryValuesByte bv,
            boolean outsideAtThreshold,
            boolean useZ,
            boolean ignoreAtThreshold) {
        super(bv, outsideAtThreshold, useZ, ignoreAtThreshold);
    }

    /**
     * This method is deliberately not broken into smaller pieces to avoid inlining.
     *
     * <p>This efficiency matters as it is called so many times over a large image.
     *
     * <p>Apologies that it is difficult to read with high cognitive-complexity.
     */
    @Override
    public boolean accptPos(int ind, Point3i point) {

        ByteBuffer inArrZ = inSlices.getLocal(0);
        ByteBuffer inArrZLess1 = inSlices.getLocal(-1);
        ByteBuffer inArrZPlus1 = inSlices.getLocal(+1);

        int xLength = extent.getX();

        int x = point.getX();
        int y = point.getY();

        if (bv.isOff(inArrZ.get(ind))) {
            return false;
        }

        // We walk up and down in x
        x--;
        ind--;
        if (x >= 0) {
            if (bv.isOff(inArrZ.get(ind))) {
                return true;
            }
        } else {
            if (!ignoreAtThreshold && !outsideAtThreshold) {
                return true;
            }
        }

        x += 2;
        ind += 2;
        if (x < extent.getX()) {
            if (bv.isOff(inArrZ.get(ind))) {
                return true;
            }
        } else {
            if (!ignoreAtThreshold && !outsideAtThreshold) {
                return true;
            }
        }
        ind--;

        // We walk up and down in y
        y--;
        ind -= xLength;
        if (y >= 0) {
            if (bv.isOff(inArrZ.get(ind))) {
                return true;
            }
        } else {
            if (!ignoreAtThreshold && !outsideAtThreshold) {
                return true;
            }
        }

        y += 2;
        ind += (2 * xLength);
        if (y < (extent.getY())) {
            if (bv.isOff(inArrZ.get(ind))) {
                return true;
            }
        } else {
            if (!ignoreAtThreshold && !outsideAtThreshold) {
                return true;
            }
        }
        ind -= xLength;

        if (useZ) {

            if (inArrZLess1 != null) {
                if (bv.isOff(inArrZLess1.get(ind))) {
                    return true;
                }
            } else {
                if (!ignoreAtThreshold && !outsideAtThreshold) {
                    return true;
                }
            }

            if (inArrZPlus1 != null) {
                if (bv.isOff(inArrZPlus1.get(ind))) {
                    return true;
                }
            } else {
                if (!ignoreAtThreshold && !outsideAtThreshold) {
                    return true;
                }
            }
        }

        return false;
    }
}
