/* (C)2020 */
package org.anchoranalysis.bean.define;

import java.util.List;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.define.adder.DefineAdder;
import org.anchoranalysis.bean.define.adder.DefineAdderUtilities;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.anchoranalysis.bean.xml.factory.HelperListUtilities;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.SubnodeConfiguration;

class AddFromSubconfigKey implements DefineAdder {

    private String key;
    private SubnodeConfiguration subConfig;
    private Object param;

    public AddFromSubconfigKey(String key, SubnodeConfiguration subConfig, Object param) {
        super();
        this.key = key;
        this.subConfig = subConfig;
        this.param = param;
    }

    @Override
    public void addTo(Define define) throws BeanXmlException {

        try {
            // A list of lists
            List<List<NamedBean<?>>> list = HelperListUtilities.listOfBeans(key, subConfig, param);
            for (List<NamedBean<?>> item : list) {
                DefineAdderUtilities.addBeansFromList(define, item);
            }
        } catch (ConfigurationRuntimeException e) {
            assert e.getCause() != null;
            // We rely on there being a cause when a configuration-runtime exception is thrown
            throw new BeanXmlException(
                    String.format("An error occurred when loading bean: %s", key), e.getCause());
        } catch (Exception e) {
            throw new BeanXmlException(
                    String.format("An error occurred when loading bean: %s", key), e);
        }
    }
}
