/*-
 * #%L
 * anchor-annotation-io
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

package org.anchoranalysis.annotation.io.input;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.CallableWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.image.stack.NamedStacks;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequenceStore;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class OperationCreateStackCollection
        implements CallableWithProgressReporter<NamedProvider<Stack>, CreateException> {

    private final ProvidesStackInput inputObject;
    private final int seriesNum;
    private final int t;

    public OperationCreateStackCollection(ProvidesStackInput inputObject) {
        this(inputObject, 0, 0);
    }

    @Override
    public NamedStacks call(ProgressReporter progressReporter) throws CreateException {
        try {
            return doOperationWithException(progressReporter);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }

    private NamedStacks doOperationWithException(ProgressReporter progressReporter)
            throws OperationFailedException {
        NamedStacks stackCollection = new NamedStacks();
        inputObject.addToStoreInferNames(
                new WrapStackAsTimeSequenceStore(stackCollection, t), seriesNum, progressReporter);
        return stackCollection;
    }
}
