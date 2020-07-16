/* (C)2020 */
package org.anchoranalysis.bean.xml.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * Utilities for manipulating list of beans
 *
 * @author Owen Feehan
 */
public class HelperListUtilities {

    private HelperListUtilities() {}

    /**
     * A list of instantiated bean objects from an XML configuration
     *
     * @param key key to match inside bean XML definition (name of the bean)
     * @param config the config associated with the XML definition
     * @param param the parameter that is used in creation of beans
     * @return
     */
    public static <T> List<T> listOfBeans(
            String key, HierarchicalConfiguration config, Object param) {
        List<HierarchicalConfiguration> fields = config.configurationsAt(key);

        ArrayList<T> retList = new ArrayList<>(fields.size());

        addListOfBeansToCollection(key, config, retList, param);
        return retList;
    }

    // gets a list of instantiated bean objects from an XML configuration
    public static <T> void addListOfBeansToCollection(
            String key, HierarchicalConfiguration config, Collection<T> addList, Object param) {
        List<HierarchicalConfiguration> fields = config.configurationsAt(key);

        for (Iterator<HierarchicalConfiguration> it = fields.iterator(); it.hasNext(); ) {
            HierarchicalConfiguration subsubConfig = it.next();

            T bean = HelperUtilities.createBeanFromConfig(subsubConfig, param);
            addList.add(bean);
        }
    }
}
