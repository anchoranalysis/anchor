package org.anchoranalysis.image.core.stack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.math.arithmetic.DoubleUtilities;

/**
 * The GPS coordinates associated with an image.
 *
 * <p>It has a custom equality method to handle floating point precision issues.
 *
 * <p>No {@code hashCode()} implementation is provided because the {@code latitude} and {@code
 * longitude} are floating-point numbers.
 */
@AllArgsConstructor
public class ImageLocation {

    /** the latitude, in degrees (can be positive or negative). */
    @Getter private double latitude;

    /** the longitude, in degrees (can be positive or negative). */
    @Getter private double longitude;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ImageLocation objCast) {
            return DoubleUtilities.areEqual(latitude, objCast.latitude)
                    && DoubleUtilities.areEqual(longitude, objCast.longitude);
        } else {
            return false;
        }
    }
}
