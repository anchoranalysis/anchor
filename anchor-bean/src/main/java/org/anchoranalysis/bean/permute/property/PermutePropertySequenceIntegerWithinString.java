/* (C)2020 */
package org.anchoranalysis.bean.permute.property;

import java.util.Iterator;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;

/** @author Owen Feehan */
public class PermutePropertySequenceIntegerWithinString extends PermutePropertySequence<String> {

    // START BEAN PROPERTIES
    @BeanField @AllowEmpty private String prefix = "";

    @BeanField @AllowEmpty private String suffix = "";
    // END BEAN PROPERTIES

    @Override
    public Iterator<String> propertyValues() {
        return new IteratorIntegerWithPrefixSuffix(range(), prefix, suffix);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String nameForPropValue(String value) {
        return value;
    }
}
