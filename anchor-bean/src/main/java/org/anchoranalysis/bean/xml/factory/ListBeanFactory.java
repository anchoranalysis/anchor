/* (C)2020 */
package org.anchoranalysis.bean.xml.factory;

import java.util.List;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;

/**
 * Factory for creating a java.util.List of Beans
 *
 * <p>Several {@code <item>someitem</item>} tags can be placed in the BeanXML and each becomes an
 * item in the list
 *
 * <pre>{@code
 * <list config-class="java.util.List" config-factory="list">
 * 	<item config-class="someclass"/>
 * 	<item config-class="someclass"/>
 *   <item config-class="someclass"/>
 * </list>
 * }</pre>
 *
 * @author Owen Feehan
 * @param <T> list-item type
 */
public class ListBeanFactory<T> extends AnchorBeanFactory {

    private ICreateFromList<T> creator;

    public ListBeanFactory() {
        // FOR BEAN INITIALIZATION
    }

    public ListBeanFactory(ICreateFromList<T> creator) {
        super();
        this.creator = creator;
    }

    // Creates the bean. Checks if already an instance exists.
    @Override
    @SuppressWarnings("rawtypes")
    public synchronized Object createBean(Class beanClass, BeanDeclaration decl, Object param)
            throws Exception {
        XMLBeanDeclaration declXML = (XMLBeanDeclaration) decl;
        SubnodeConfiguration subConfig = declXML.getConfiguration();

        List<T> list = HelperListUtilities.listOfBeans("item", subConfig, param);
        if (creator != null) {
            return creator.create(list);
        } else {
            return list;
        }
    }
}
