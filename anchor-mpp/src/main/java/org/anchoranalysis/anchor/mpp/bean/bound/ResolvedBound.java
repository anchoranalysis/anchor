/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.bound;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.random.RandomNumberGenerator;

/** A bound resolved into pixel units */
@NoArgsConstructor
@AllArgsConstructor
public class ResolvedBound extends AnchorBean<ResolvedBound> implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private double min = 0.0;

    @BeanField @Getter @Setter private double max = 1.0;
    // END BEAN PROPERTIES

    public ResolvedBound(ResolvedBound src) {
        this.min = src.min;
        this.max = src.max;
    }

    public boolean contains(double val) {
        return (val >= min && val <= max);
    }

    public String getDscr() {
        return String.format("resolvedBound(min=%f,max=%f)", getMin(), getMax());
    }

    public void scale(double multFactor) {
        this.min = this.min * multFactor;
        this.max = this.max * multFactor;
    }

    /** A random value between the bounds (open interval) */
    public double randOpen(RandomNumberGenerator randomNumberGenerator) {
        return randomNumberGenerator.sampleDoubleFromRange(min, max);
    }

    @Override
    public String toString() {
        return String.format("%f-%f", min, max);
    }
}
