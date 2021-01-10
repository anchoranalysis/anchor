package org.anchoranalysis.core.index.bounded.bridge;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.bounded.BoundChangeListener;
import org.anchoranalysis.core.index.bounded.BoundedIndexContainer;

/**
 * Bridges calls from hidden-type to external-type.
 *
 * <p>See {@link org.anchoranalysis.core.index.bounded.bridge.BoundedIndexContainerBridgeWithIndex}
 *
 * @author Owen Feehan
 * @param <H> hidden-type (type passed to the delegate)
 * @param <S> external-type (type exposed in an interface from this class)
 */
@AllArgsConstructor
public abstract class BoundedIndexContainerBridge<H, S, E extends Exception>
        implements BoundedIndexContainer<S> {

    /** The source container that is <i>bridged</i>. */
    private BoundedIndexContainer<H> source;

    @Override
    public S get(int index) throws GetOperationFailedException {

        H internalState = source.get(index);
        try {
            return bridge(index, internalState);
        } catch (Exception e) {
            throw new GetOperationFailedException(index, e);
        }
    }

    protected abstract S bridge(int index, H internalState) throws E;

    @Override
    public void addBoundChangeListener(BoundChangeListener cl) {
        this.source.addBoundChangeListener(cl);
    }

    @Override
    public int nextIndex(int index) {
        return this.source.nextIndex(index);
    }

    @Override
    public int previousIndex(int index) {
        return this.source.previousIndex(index);
    }

    @Override
    public int getMinimumIndex() {
        return this.source.getMinimumIndex();
    }

    @Override
    public int getMaximumIndex() {
        return this.source.getMaximumIndex();
    }

    @Override
    public int previousEqualIndex(int index) {
        return this.source.previousEqualIndex(index);
    }
}
