/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.bean.mark;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.feature.bean.nrgscheme.NRGSchemeCreator;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.feature.bean.FeatureRelatedBean;

public class MarkEvaluator extends FeatureRelatedBean<MarkEvaluator> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private CfgGen cfgGen;

    @BeanField @Getter @Setter private Define define;

    @BeanField @Getter @Setter private NRGSchemeCreator nrgSchemeCreator;
    // END BEAN PROPERTIES
}
