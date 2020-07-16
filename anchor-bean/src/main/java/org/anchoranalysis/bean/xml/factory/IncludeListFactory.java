/* (C)2020 */
package org.anchoranalysis.bean.xml.factory;

import java.util.List;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;

/**
 * @author Owen Feehan
 * @param <T> list-item type
 */
public class IncludeListFactory<T> extends AnchorBeanFactory {

    public IncludeListFactory() {
        // FOR BEAN INITIALIZATION
    }

    // Creates the bean. Checks if already an instance exists.
    @Override
    @SuppressWarnings("rawtypes")
    public synchronized Object createBean(Class beanClass, BeanDeclaration decl, Object param)
            throws Exception {
        XMLBeanDeclaration declXML = (XMLBeanDeclaration) decl;
        SubnodeConfiguration subConfig = declXML.getConfiguration();

        List<T> list = HelperListUtilities.listOfBeans("item", subConfig, param);

        if (!subConfig.configurationsAt("include").isEmpty()) {
            SubnodeConfiguration includeConfig = subConfig.configurationAt("include");

            List<List<T>> listInclude =
                    HelperListUtilities.listOfBeans("item", includeConfig, param);
            for (List<T> include : listInclude) {
                list.addAll(0, include);
            }
        }
        return list;
    }
}
