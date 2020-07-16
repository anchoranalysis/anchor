/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.bound;

import java.io.Serializable;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.image.extent.ImageResolution;

@GroupingRoot
public abstract class MarkBounds extends AnchorBean<MarkBounds> implements Serializable {

    /** */
    private static final long serialVersionUID = 0;

    public abstract double getMinResolved(ImageResolution sr, boolean do3D);

    public abstract double getMaxResolved(ImageResolution sr, boolean do3D);

    public ResolvedBound calcMinMax(ImageResolution sr, boolean do3D) {
        return new ResolvedBound(getMinResolved(sr, do3D), getMaxResolved(sr, do3D));
    }
}
