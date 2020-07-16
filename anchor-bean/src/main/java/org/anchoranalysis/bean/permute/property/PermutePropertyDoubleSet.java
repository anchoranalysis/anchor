/* (C)2020 */
package org.anchoranalysis.bean.permute.property;

import java.util.Iterator;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.StringSet;
import org.anchoranalysis.bean.annotation.BeanField;

public class PermutePropertyDoubleSet extends PermutePropertyWithPath<Double> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private StringSet values;
    // END BEAN PROPERTIES

    @Override
    public Iterator<Double> propertyValues() {
        return values.set().stream().mapToDouble(Double::parseDouble).iterator(); // NOSONAR
    }

    @Override
    public String nameForPropValue(Double value) {
        return value.toString();
    }
}
