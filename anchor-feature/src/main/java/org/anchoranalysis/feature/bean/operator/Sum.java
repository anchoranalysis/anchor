/* (C)2020 */
package org.anchoranalysis.feature.bean.operator;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

public class Sum<T extends FeatureInput> extends FeatureListElem<T> {

    // START BEAN PROPERTIES
    /** If TRUE, we ignore any NaN values. Otherwise the sum becomes NaN */
    private boolean ignoreNaN;
    // END BEAN PROPERTIES

    public Sum() {
        // Standard bean constructor
    }

    public Sum(FeatureList<T> list) {
        super(list);
    }

    @Override
    public double calc(SessionInput<T> input) throws FeatureCalcException {

        double result = 0;

        for (Feature<T> elem : getList()) {
            double resultInd = input.calc(elem);

            if (ignoreNaN && Double.isNaN(resultInd)) {
                continue;
            }

            result += resultInd;
        }

        return result;
    }

    @Override
    public String getDscrLong() {
        return descriptionForList("+");
    }

    public boolean isIgnoreNaN() {
        return ignoreNaN;
    }

    public void setIgnoreNaN(boolean ignoreNaN) {
        this.ignoreNaN = ignoreNaN;
    }
}
