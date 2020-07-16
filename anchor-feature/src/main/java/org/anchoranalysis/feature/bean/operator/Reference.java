/* (C)2020 */
package org.anchoranalysis.feature.bean.operator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

@NoArgsConstructor
public class Reference<T extends FeatureInput> extends FeatureOperator<T> {

    // START BEAN
    @BeanField @Getter @Setter private String id;
    // END BEAN

    public Reference(String id) {
        super();
        this.id = id;
    }

    @Override
    public double calc(SessionInput<T> input) throws FeatureCalcException {
        // We resolve the ID before its passed to calcFeatureByID
        String resolvedID = input.bySymbol().resolveFeatureID(id);
        return input.bySymbol().calcFeatureByID(resolvedID, input);
    }

    @Override
    public String getDscrLong() {
        return "_" + id;
    }
}
