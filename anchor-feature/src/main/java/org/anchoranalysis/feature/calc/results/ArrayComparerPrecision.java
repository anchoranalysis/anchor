/* (C)2020 */
package org.anchoranalysis.feature.calc.results;

import org.apache.commons.math3.util.Precision;

class ArrayComparerPrecision extends ArrayComparer {

    private double eps;

    public ArrayComparerPrecision(double eps) {
        this.eps = eps;
    }

    @Override
    protected boolean compareItem(Object obj1, Object obj2) {

        // Special case for doubles
        if (obj1 instanceof Double) {
            return compareDoubleWithOther((Double) obj1, obj2);
        } else {
            return super.compareItem(obj1, obj2);
        }
    }

    private boolean compareDoubleWithOther(Double dbl, Object other) {
        if (other instanceof Double) {
            return Precision.equals(dbl, (Double) other, eps);
        } else {
            return false;
        }
    }
}
