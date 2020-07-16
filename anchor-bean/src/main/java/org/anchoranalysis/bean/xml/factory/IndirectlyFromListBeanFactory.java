/* (C)2020 */
package org.anchoranalysis.bean.xml.factory;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;

public class IndirectlyFromListBeanFactory<T extends AnchorBean<T>, S> extends AnchorBeanFactory {

    private FunctionWithException<List<T>, S, ? extends Exception> bridge;

    public IndirectlyFromListBeanFactory(
            FunctionWithException<List<T>, S, ? extends Exception> bridge) {
        this.bridge = bridge;
    }

    // Creates the bean. Checks if already an instance exists.
    @Override
    @SuppressWarnings("rawtypes")
    public synchronized Object createBean(Class beanClass, BeanDeclaration decl, Object param)
            throws Exception {
        XMLBeanDeclaration declXML = (XMLBeanDeclaration) decl;
        SubnodeConfiguration subConfig = declXML.getConfiguration();

        List<T> list = new ArrayList<>();
        HelperListUtilities.addListOfBeansToCollection(
                "mapEntryList.mapEntry", subConfig, list, param);
        return bridge.apply(list);
    }
}
