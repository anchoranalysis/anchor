/* (C)2020 */
package org.anchoranalysis.bean.xml.factory;

import org.anchoranalysis.bean.StringList;
import org.anchoranalysis.bean.StringSet;
import org.apache.commons.configuration.beanutils.BeanDeclaration;

/**
 * A factory for creating {@link StringSet} beans
 *
 * @author Owen Feehan
 */
public class StringListFactory extends AnchorBeanFactory {

    // Creates the bean. Checks if already an instance exists.
    @Override
    @SuppressWarnings("rawtypes")
    public synchronized Object createBean(Class beanClass, BeanDeclaration decl, Object param)
            throws Exception {
        return HelperUtilities.populateStringCollectionFromXml(new StringList(), decl);
    }
}
