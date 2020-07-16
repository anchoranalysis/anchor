/* (C)2020 */
package org.anchoranalysis.feature.bean.operator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.input.FeatureInput;

@NoArgsConstructor
public class Constant<T extends FeatureInput> extends FeatureOperator<T> {

    // START BEAN PARAMETERS
    @BeanField @Getter @Setter private double value;
    // END BEAN PARAMETERS

    /**
     * Constructor with a particular value
     *
     * @param value value
     */
    public Constant(double value) {
        this.value = value;
    }

    /**
     * Constructor with a particular value and a custom-name
     *
     * @param customName custom-name for feature
     * @param value value
     */
    public Constant(String customName, double value) {
        this.value = value;
        setCustomName(customName);
    }

    @Override
    public double calc(SessionInput<T> input) {
        return value;
    }

    @Override
    public String getParamDscr() {
        return String.format("%2.2f", value);
    }

    @Override
    public String getDscrLong() {
        return getParamDscr();
    }
}
