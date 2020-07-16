/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark;

import java.util.Arrays;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.orientation.Orientation;

public abstract class MarkConic extends MarkAbstractPosition {

    /** */
    private static final long serialVersionUID = 1680124471263339009L;

    public MarkConic() {
        super();
    }

    public MarkConic(MarkAbstractPosition src) {
        super(src);
    }

    public abstract double[] createRadiiArrayResolved(ImageResolution sr);

    public abstract double[] createRadiiArray();

    public abstract void setMarksExplicit(Point3d pos, Orientation orientation, Point3d radii);

    public abstract void setMarksExplicit(Point3d pos, Orientation orientation);

    public abstract void setMarksExplicit(Point3d pos);

    public double[] radiiOrderedResolved(ImageResolution sr) {
        double[] radii = createRadiiArrayResolved(sr);
        Arrays.sort(radii);
        return radii;
    }

    public double[] radiiOrdered() {
        double[] radii = createRadiiArray();
        Arrays.sort(radii);
        return radii;
    }
}
