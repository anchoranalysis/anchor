/* (C)2020 */
package org.anchoranalysis.bean.xml.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.StringBeanCollection;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.BeanHelper;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HelperUtilities {

    public static <T> T createBeanFromXML(
            XMLBeanDeclaration declXML, String configurationKey, Object param) {
        HierarchicalConfiguration itemConfig =
                declXML.getConfiguration().configurationAt(configurationKey);
        T bean = HelperUtilities.createBeanFromConfig(itemConfig, param); // NOSONAR
        return bean;
    }

    /*** Creates a bean from a HierarchicalConfiguration */
    public static <T> T createBeanFromConfig(HierarchicalConfiguration config, Object param) {
        BeanDeclaration subDecl = new XMLBeanDeclaration(config, ".");

        @SuppressWarnings("unchecked")
        T bean = (T) BeanHelper.createBean(subDecl, null, param);

        return bean;
    }

    public static StringBeanCollection populateStringCollectionFromXml(
            StringBeanCollection collection, BeanDeclaration decl) {

        XMLBeanDeclaration declXML = (XMLBeanDeclaration) decl;
        SubnodeConfiguration subConfig = declXML.getConfiguration();

        String[] stringArr = subConfig.getStringArray("item");
        for (String item : stringArr) {
            collection.add(item);
        }

        return collection;
    }
}
