/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.transformer;

import org.anchoranalysis.bean.AnchorBean;

/**
 * Transforms one type into another
 *
 * @author Owen Feehan
 * @param <S> source-type for transformation
 * @param <T> destination-type for transformation
 */
public abstract class StateTransformerBean<S, T> extends AnchorBean<StateTransformerBean<S, T>>
        implements StateTransformer<S, T> {}
