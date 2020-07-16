/* (C)2020 */
package org.anchoranalysis.bean.define.adder;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.anchoranalysis.core.error.OperationFailedException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefineAdderUtilities {

    public static void addBeansFromList(Define out, List<NamedBean<?>> list)
            throws BeanXmlException {

        try {
            for (NamedBean<?> ni : list) {
                out.add(ni);
            }
        } catch (OperationFailedException e) {
            throw new BeanXmlException(e);
        }
    }
}
