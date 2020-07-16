/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.bound;

import org.anchoranalysis.image.extent.ImageResolution;

//
//  An upper and lower bound
//
public abstract class Bound extends MarkBounds {

    /** */
    private static final long serialVersionUID = -5447041367811327604L;

    public abstract Bound duplicate();

    public ResolvedBound resolve(ImageResolution sr, boolean do3D) {
        return new ResolvedBound(getMinResolved(sr, do3D), getMaxResolved(sr, do3D));
    }

    public abstract void scale(double multFactor);
}
