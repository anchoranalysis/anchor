/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.stack.input;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.core.progress.ProgressIgnore;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.TimeSequence;
import org.anchoranalysis.image.core.stack.named.NamedStacks;
import org.anchoranalysis.image.core.stack.time.WrapStackAsTimeSequenceStore;
import org.anchoranalysis.io.input.InputFromManager;

/**
 * Base class for inputs which somehow eventually send up providing stacks, with or without names.
 *
 * @author Owen Feehan
 */
public interface ProvidesStackInput extends InputFromManager {

    /**
     * Exposes the input as a set of named stacks (inferring the names).
     *
     * @param progress a progress-reporter.
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @return a set of named-stacks.
     * @throws OperationFailedException
     */
    default NamedStacks asSet(Progress progress, Logger logger) throws OperationFailedException {
        NamedStacks set = new NamedStacks();
        addToStoreInferNames(new WrapStackAsTimeSequenceStore(set, 0), 0, progress, logger);
        return set;
    }

    /**
     * Adds the current object to a named-store of stacks (using the default series).
     *
     * @param store the store.
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @throws OperationFailedException
     */
    default void addToStoreInferNames(NamedProviderStore<Stack> store, Logger logger)
            throws OperationFailedException {
        addToStoreInferNames(
                new WrapStackAsTimeSequenceStore(store),
                0, // default series-number
                ProgressIgnore.get(),
                logger);
    }

    /**
     * Adds the current object to a named-store of stacks.
     *
     * @param stacks
     * @param seriesIndex
     * @param progress
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @throws OperationFailedException
     */
    void addToStoreInferNames(
            NamedProviderStore<TimeSequence> stacks,
            int seriesIndex,
            Progress progress,
            Logger logger)
            throws OperationFailedException;

    void addToStoreWithName(
            String name,
            NamedProviderStore<TimeSequence> stacks,
            int seriesIndex,
            Progress progress,
            Logger logger)
            throws OperationFailedException;

    int numberFrames() throws OperationFailedException;
}
