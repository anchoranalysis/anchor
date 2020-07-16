/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.bound;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;

public abstract class BoundMinMax extends Bound {

    /** */
    private static final long serialVersionUID = -493791653577617743L;

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ResolvedBound delegate;
    // END BEAN PROPERTIES

    public BoundMinMax() {
        delegate = new ResolvedBound();
    }

    public BoundMinMax(double min, double max) {
        delegate = new ResolvedBound(min, max);
    }

    public BoundMinMax(BoundMinMax src) {
        delegate = new ResolvedBound(src.delegate);
    }

    // Getters and Setters
    public double getMin() {
        return delegate.getMin();
    }

    public void setMin(double min) {
        delegate.setMin(min);
    }

    public double getMax() {
        return delegate.getMax();
    }

    public void setMax(double max) {
        delegate.setMax(max);
    }

    @Override
    public String getBeanDscr() {
        return delegate.getDscr();
    }

    @Override
    public void scale(double multFactor) {
        delegate.scale(multFactor);
    }
}
