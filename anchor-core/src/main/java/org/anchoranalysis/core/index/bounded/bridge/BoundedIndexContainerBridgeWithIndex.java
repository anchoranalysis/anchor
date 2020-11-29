/*-
 * #%L
 * anchor-core
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
package org.anchoranalysis.core.index.bounded.bridge;

import org.anchoranalysis.core.index.BridgeElementWithIndex;
import org.anchoranalysis.core.index.bounded.BoundedIndexContainer;

/**
 * Bridges calls from hidden-type to external-type. Uses an IObjectBridgeIndex for the bridging.
 *
 * <p>See {@link
 * org.anchoranalysis.core.index.bounded.bridge.BoundedIndexContainerBridgeWithoutIndex}
 *
 * @author Owen Feehan
 * @param <H> hidden-type (type passed to the delegate)
 * @param <S> external-type (type exposed in an interface from this class)
 * @param <E> exception that can be thrown during briging
 */
public class BoundedIndexContainerBridgeWithIndex<H, S, E extends Exception>
        extends BoundedIndexContainerBridge<H, S, E> {

    private BridgeElementWithIndex<H, S, E> bridge;

    public BoundedIndexContainerBridgeWithIndex(
            BoundedIndexContainer<H> source, BridgeElementWithIndex<H, S, E> bridge) {
        super(source);
        this.bridge = bridge;
    }

    @Override
    protected S bridge(int index, H internalState) throws E {
        return bridge.bridgeElement(index, internalState);
    }
}
