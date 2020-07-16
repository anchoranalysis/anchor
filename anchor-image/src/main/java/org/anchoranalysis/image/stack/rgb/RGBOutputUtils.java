/* (C)2020 */
package org.anchoranalysis.image.stack.rgb;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.ImageDimensions;

public class RGBOutputUtils {

    private RGBOutputUtils() {}

    public static void writeRGBColorToByteArr(
            RGBColor c,
            Point3i point,
            ImageDimensions sd,
            ByteBuffer red,
            ByteBuffer blue,
            ByteBuffer green) {
        int index = sd.offset(point.getX(), point.getY(), 0);
        red.put(index, (byte) c.getRed());
        green.put(index, (byte) c.getGreen());
        blue.put(index, (byte) c.getBlue());
    }
}
