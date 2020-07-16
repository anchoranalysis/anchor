/* (C)2020 */
package org.anchoranalysis.bean.xml.mock;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;

public class MockBeanNested extends AnchorBean<MockBeanNested> {

    @BeanField @Getter @Setter private String fieldSimpleNecessary;

    @BeanField @AllowEmpty @Getter @Setter private String fieldSimpleAllowEmpty = "";

    @BeanField @Getter @Setter private MockBeanSimple fieldBeanNecessary;

    @BeanField @OptionalBean @Getter @Setter private MockBeanSimple fieldBeanOptional;
}
