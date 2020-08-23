/*-
 * #%L
 * anchor-mpp-sgmn
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.mpp.sgmn.optscheme;

import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.mpp.sgmn.transformer.StateTransformer;
import org.anchoranalysis.mpp.sgmn.transformer.TransformationContext;
import lombok.Getter;

/**
 * Remembers the current state of type T and some best state (which was once set from the current
 * state)
 *
 * @author Owen Feehan
 * @param <T>
 */
public class DualState<T> {

    @Getter private Optional<T> current;
    @Getter private Optional<T> best;

    public DualState() {
        this.current = Optional.empty();
        this.best = Optional.empty();
    }

    private DualState(Optional<T> current, Optional<T> best) {
        super();
        this.current = current;
        this.best = best;
    }

    public T releaseKeepBest() throws OperationFailedException {
        T kept = this.best.orElseThrow(DualState::noBestDefined);
        this.current = Optional.empty();
        clearBest();
        return kept;
    }
    
    public void assignCurrent(T stateToAssign) {
        this.current = Optional.of(stateToAssign);
    }

    public void assignBestFromCurrent() {
        this.best = this.current;
    }

    public void clearBest() {
        this.best = Optional.empty();
    }

    public <S> DualState<S> transform(StateTransformer<T, S> func, TransformationContext context)
            throws OperationFailedException {
        return new DualState<>(
                transformOptional(current, func, context), transformOptional(best, func, context));
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
