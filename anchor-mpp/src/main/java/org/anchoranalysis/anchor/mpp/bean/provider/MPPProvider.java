/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.provider;

import org.anchoranalysis.anchor.mpp.bean.MPPBean;
import org.anchoranalysis.bean.Provider;

/**
 * @author Owen Feehan
 * @param <T> bean-type
 * @param <S> provider-type
 */
public abstract class MPPProvider<T, S> extends MPPBean<T> implements Provider<S> {}
