/* (C)2020 */
package org.anchoranalysis.bean.define.adder;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.xml.error.BeanXmlException;

/** Concatenates a list of adders */
public class Concatenate extends DefineAdderBean {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private List<DefineAdderBean> list = new ArrayList<>();
    // END BEAN PROPERTIES

    @Override
    public void addTo(Define define) throws BeanXmlException {

        for (DefineAdderBean da : list) {
            da.addTo(define);
        }
    }
}
