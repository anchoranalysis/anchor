/* (C)2020 */
package org.anchoranalysis.feature.nrg;

import java.io.Serializable;

public class NRGTotal implements Serializable {

    /** */
    private static final long serialVersionUID = -595099053761257083L;

    double total;

    public NRGTotal() {
        total = 0;
    }

    public NRGTotal(double total) {
        this.total = total;
    }

    public NRGTotal deepCopy() {
        NRGTotal out = new NRGTotal();
        out.total = total;
        return out;
    }

    public final void add(double val) {
        total += val;
    }

    public final double getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return String.format("%8.3f", this.total);
    }
}
