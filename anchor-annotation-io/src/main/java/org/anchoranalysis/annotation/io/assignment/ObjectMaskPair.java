/* (C)2020 */
package org.anchoranalysis.annotation.io.assignment;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.ObjectMask;

@Value
@AllArgsConstructor
class ObjectMaskPair {

    private final ObjectMask left;
    private final ObjectMask right;
    private final double overlapRatio;

    public boolean atBorderXY(ImageDimensions sd) {
        return left.getBoundingBox().atBorderXY(sd) || right.getBoundingBox().atBorderXY(sd);
    }

    public ObjectMask getMultiplex(boolean leftFlag) {
        return leftFlag ? left : right;
    }
}
