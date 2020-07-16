/* (C)2020 */
package org.anchoranalysis.io.bioformats.copyconvert.tobyte;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ConvertHelper {

    public static float twoToPower(int exponent) {
        return (float) Math.pow(2.0, exponent);
    }
}
