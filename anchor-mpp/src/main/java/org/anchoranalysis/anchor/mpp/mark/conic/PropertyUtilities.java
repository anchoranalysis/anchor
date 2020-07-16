/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark.conic;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PropertyUtilities {

    public static void addPoint2dProperty(
            ObjectWithProperties mask, String propertyName, double x, double y) {
        mask.setProperty(propertyName, new Point3d(x, y, 0));
    }
}
