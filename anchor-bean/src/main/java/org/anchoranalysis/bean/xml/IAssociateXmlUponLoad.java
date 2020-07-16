/* (C)2020 */
package org.anchoranalysis.bean.xml;

import org.apache.commons.configuration.XMLConfiguration;

/**
 * For classes that wish to have a XmlConfiguration associated with them
 *
 * <p>The associateXml method is called after the class has been loaded with the BeanXMLLoader
 *
 * @author Owen Feehan
 */
@FunctionalInterface
public interface IAssociateXmlUponLoad {

    /**
     * Associated
     *
     * @param xmlConfiguration the xml-configuration to associate with an object
     */
    void associateXml(XMLConfiguration xmlConfiguration);
}
