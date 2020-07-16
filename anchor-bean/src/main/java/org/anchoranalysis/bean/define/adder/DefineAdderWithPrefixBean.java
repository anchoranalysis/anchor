/* (C)2020 */
package org.anchoranalysis.bean.define.adder;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.anchoranalysis.core.error.OperationFailedException;

/**
 * Adds a prefix
 *
 * @author Owen Feehan
 */
public abstract class DefineAdderWithPrefixBean extends DefineAdderBean {

    // START BEAN PROPERTIES
    /** A prefix that is placed before the name of every bean created */
    @BeanField @AllowEmpty @Getter @Setter private String prefix = "";
    // END BEAN PROPERTIES

    protected void addWithName(Define define, String name, AnchorBean<?> item)
            throws BeanXmlException {
        NamedBean<?> nb = new NamedBean<>(resolveName(name), item);
        try {
            define.add(nb);
        } catch (OperationFailedException e) {
            throw new BeanXmlException(e);
        }
    }

    /** Adds a prefix before the name */
    protected String resolveName(String name) {
        return prefix + name;
    }
}
