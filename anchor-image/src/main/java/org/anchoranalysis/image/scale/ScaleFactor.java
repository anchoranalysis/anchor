/* (C)2020 */
package org.anchoranalysis.image.scale;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * What to scale x and y dimensions by
 *
 * <p>This class is IMMUTABLE.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public final class ScaleFactor {

    private final double x;
    private final double y;

    public ScaleFactor(double factor) {
        this(factor, factor);
    }

    public ScaleFactor invert() {
        return new ScaleFactor(1 / x, 1 / y);
    }

    public boolean hasIdenticalXY() {
        return Math.abs(x - y) < 1e-3;
    }

    /** If the scale-factor involves no scaling at all */
    public boolean isNoScale() {
        return x == 1.0 && y == 1.0;
    }

    @Override
    public String toString() {
        return String.format("x=%f\ty=%f\t\tx^-1=%f\ty^-1=%f", x, y, 1 / x, 1 / y);
    }
}
