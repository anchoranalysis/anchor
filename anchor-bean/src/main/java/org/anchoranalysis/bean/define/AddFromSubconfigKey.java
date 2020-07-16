/*-
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
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
