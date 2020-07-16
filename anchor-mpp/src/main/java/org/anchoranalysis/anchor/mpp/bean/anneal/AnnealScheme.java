/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.anneal;

import org.anchoranalysis.bean.AnchorBean;

public abstract class AnnealScheme extends AnchorBean<AnnealScheme> {

    public abstract double calcTemp(int iter);

    public abstract double crntTemp();

    public double calcDensityRatio(double num, double dem, int iter) {

        double d = (num - dem) / calcTemp(iter);
        double r = Math.exp(d);
        assert (!Double.isNaN(r));
        return r;
    }
}
