/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.io.stack.time;

import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.StoreSupplier;
import org.anchoranalysis.image.core.stack.Stack;

/**
 * Exposes a {@code NamedProviderStore<TimeSeries>} as a {@code NamedProviderStore<Stack>} by
 * extracting a frame from each series.
 *
 * @author Owen Feehan
 */
public class ExtractFrameStore implements NamedProviderStore<TimeSeries> {

    private final NamedProviderStore<Stack> stacks;

    /** The time-index to extract from each {@link TimeSeries}. */
    private final int timeIndex;

    /**
     * Creates to extract at time-index 0.
     *
     * @param stacks the underlying store of {@link Stack}s.
     */
    public ExtractFrameStore(NamedProviderStore<Stack> stacks) {
        this(stacks, 0);
    }

    /**
     * Creates to extract at time-index {@code timeIndex}.
     *
     * @param stacks the underlying store of {@link Stack}s.
     * @param timeIndex the time-index to extract from each {@link TimeSeries}.
     */
    public ExtractFrameStore(NamedProviderStore<Stack> stacks, int timeIndex) {
        this.stacks = stacks;
        this.timeIndex = timeIndex;
    }

    @Override
    public Optional<TimeSeries> getOptional(String key) throws NamedProviderGetException {
        return stacks.getOptional(key).map(TimeSeries::new);
    }

    @Override
    public Set<String> keys() {
        return stacks.keys();
    }

    @Override
    public void add(String identifier, StoreSupplier<TimeSeries> supplier)
            throws OperationFailedException {
        stacks.add(identifier, () -> supplier.get().getFrame(timeIndex));
    }
}
