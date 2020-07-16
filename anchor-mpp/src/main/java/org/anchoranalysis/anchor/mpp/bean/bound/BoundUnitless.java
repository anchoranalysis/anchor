/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.bound;

import org.anchoranalysis.image.extent.ImageResolution;

//
//  An upper and lower bound
//
public class BoundUnitless extends BoundMinMax {

    /** */
    private static final long serialVersionUID = 8738881592311859713L;

    public BoundUnitless() {
        super();
    }

    public BoundUnitless(double min, double max) {
        super(min, max);
    }

    public BoundUnitless(BoundUnitless src) {
        super(src);
    }

    @Override
    public String getBeanDscr() {
        return String.format("%s(min=%f,max=%f)", getBeanName(), getMin(), getMax());
    }

    @Override
    public double getMinResolved(ImageResolution sr, boolean do3D) {
        return getMin();
    }

    @Override
    public double getMaxResolved(ImageResolution sr, boolean do3D) {
        return getMax();
    }

    public double size() {
        return getMax() - getMin() + 1;
    }

    @Override
    public Bound duplicate() {
        return new BoundUnitless(this);
    }
}
