/* (C)2020 */
package org.anchoranalysis.bean.shared;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;

/**
 * A simple mapping from one string (source) to another (target)
 *
 * @author Owen Feehan
 */
public class StringMapItem extends AnchorBean<StringMapItem> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String source;

    @BeanField @Getter @Setter private String target;
    // END BEAN PROPERTIES
}
