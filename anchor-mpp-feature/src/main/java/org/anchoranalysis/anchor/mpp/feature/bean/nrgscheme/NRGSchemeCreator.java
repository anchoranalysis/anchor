/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.bean.nrgscheme;

import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGScheme;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.FeatureRelatedBean;

// Creates NRG Elem
public abstract class NRGSchemeCreator extends FeatureRelatedBean<NRGSchemeCreator> {

    public abstract NRGScheme create() throws CreateException;
}
