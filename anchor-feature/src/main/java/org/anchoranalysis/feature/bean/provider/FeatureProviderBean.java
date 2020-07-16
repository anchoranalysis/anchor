/* (C)2020 */
package org.anchoranalysis.feature.bean.provider;

import org.anchoranalysis.bean.Provider;
import org.anchoranalysis.feature.bean.FeatureRelatedBean;

/**
 * @author Owen Feehan
 * @param <B> bean-type
 * @param <P> provider-type
 */
public abstract class FeatureProviderBean<B, P> extends FeatureRelatedBean<B>
        implements Provider<P> {}
