/* (C)2020 */
package org.anchoranalysis.image.interpolator;

public class InterpolatorFactory {

    private static InterpolatorFactory instance = null;

    private static Interpolator noInterpolator = new InterpolatorNone();

    private static Interpolator resizingInterpolator = new InterpolatorImageJ();

    public static InterpolatorFactory getInstance() {
        if (instance == null) {
            instance = new InterpolatorFactory();
        }
        return instance;
    }

    private InterpolatorFactory() {}

    public Interpolator noInterpolation() {
        return noInterpolator;
    }

    public Interpolator rasterResizing() {
        return resizingInterpolator;
    }

    public Interpolator binaryResizing() {
        return resizingInterpolator;
    }
}
