/* (C)2020 */
package org.anchoranalysis.bean.permute.property;

import java.util.Iterator;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.StringSet;
import org.anchoranalysis.bean.annotation.BeanField;

public class PermutePropertyStringSet extends PermutePropertyWithPath<String> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private StringSet values;
    // END BEAN PROPERTIES

    @Override
    public Iterator<String> propertyValues() {
        return values.iterator();
    }

    @Override
    public String nameForPropValue(String value) {
        return value;
    }
}
