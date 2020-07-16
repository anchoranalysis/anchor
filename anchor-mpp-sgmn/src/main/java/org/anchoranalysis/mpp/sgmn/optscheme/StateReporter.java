/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme;

import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.mpp.sgmn.transformer.StateTransformer;

/**
 * Converts the optimization state to a form that the reporter needs
 *
 * @author Owen Feehan
 */
public abstract class StateReporter<T, S> extends AnchorBean<StateReporter<T, S>> {

    public abstract StateTransformer<T, S> primaryReport();

    public abstract Optional<StateTransformer<T, S>> secondaryReport();
}
