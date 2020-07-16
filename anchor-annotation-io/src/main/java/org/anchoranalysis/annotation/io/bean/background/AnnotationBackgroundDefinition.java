/* (C)2020 */
package org.anchoranalysis.annotation.io.bean.background;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.shared.StringMap;

public class AnnotationBackgroundDefinition extends AnchorBean<AnnotationBackgroundDefinition> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String stackNameVisualOriginal;

    /** If non-null, maps underlying stack-name to a background */
    @BeanField @OptionalBean @Getter @Setter private StringMap backgroundStackMap;

    /** If non-empty any stackNames (after map) containing a certain string will be ignored */
    @BeanField @AllowEmpty @Getter @Setter private String ignoreContains = "";
    // END BEAN PROPERTIES
}
