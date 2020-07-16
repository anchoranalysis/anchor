/* (C)2020 */
package org.anchoranalysis.bean.define.adder;

import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.xml.error.BeanXmlException;

/**
 * Adds bean-definitions to a Define class
 *
 * @author Owen Feehan
 */
@FunctionalInterface
public interface DefineAdder {

    void addTo(Define define) throws BeanXmlException;
}
