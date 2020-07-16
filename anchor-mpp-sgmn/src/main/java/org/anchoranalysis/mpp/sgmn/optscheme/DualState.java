/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme;

import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.mpp.sgmn.transformer.StateTransformer;
import org.anchoranalysis.mpp.sgmn.transformer.TransformationContext;

/**
 * Remembers the current state of type T and some best state (which was once set from the current
 * state)
 *
 * @author Owen Feehan
 * @param <T>
 */
public class DualState<T> {

    private Optional<T> crnt;
    private Optional<T> best;

    public DualState() {
        this.crnt = Optional.empty();
        this.best = Optional.empty();
    }

    private DualState(Optional<T> crnt, Optional<T> best) {
        super();
        this.crnt = crnt;
        this.best = best;
    }

    public T releaseKeepBest() throws OperationFailedException {
        T cfgNRG = this.best.orElseThrow(DualState::noBestDefined);
        this.crnt = Optional.empty();
        clearBest();
        return cfgNRG;
    }

    public Optional<T> getCrnt() {
        return crnt;
    }

    public void assignCrnt(T cfgNRG) {
        this.crnt = Optional.of(cfgNRG);
    }

    public Optional<T> getBest() {
        return best;
    }

    public void assignBestFromCrnt() {
        this.best = this.crnt;
    }

    public void clearBest() {
        this.best = Optional.empty();
    }

    public <S> DualState<S> transform(StateTransformer<T, S> func, TransformationContext context)
            throws OperationFailedException {
        return new DualState<>(
                transformOptional(crnt, func, context), transformOptional(best, func, context));
    }

    private static <T, S> Optional<S> transformOptional(
            Optional<T> optional, StateTransformer<T, S> func, TransformationContext context)
            throws OperationFailedException {
        return OptionalUtilities.map(optional, a -> func.transform(a, context));
    }

    private static OperationFailedException noBestDefined() {
        return new OperationFailedException("No `best` is currently defined");
    }
}
