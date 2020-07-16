/* (C)2020 */
package org.anchoranalysis.bean.xml.mock;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;

/**
 * A simple bean containing amessage
 *
 * @author Owen Feehan
 */
public class MockBeanSimple extends AnchorBean<MockBeanSimple> {

    @BeanField @Getter @Setter private String message;
}
