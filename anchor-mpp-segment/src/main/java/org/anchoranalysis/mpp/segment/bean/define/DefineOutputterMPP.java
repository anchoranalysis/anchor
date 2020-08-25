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

package org.anchoranalysis.mpp.segment.bean.define;

import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.io.input.series.NamedChannelsForSeries;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequenceStore;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.mpp.io.input.MultiInput;

/**
 * Helper for tasks that uses a {@link Define} in association with an input to execute some tasks,
 * and then outputs results *
 */
public class DefineOutputterMPP extends DefineOutputter {

    @FunctionalInterface
    public interface OperationWithInitParams<T> {
        void process(T initParams) throws OperationFailedException;
    }

    public void processInput(NamedChannelsForSeries ncc, BoundIOContext context)
            throws OperationFailedException {
        try {
            MPPInitParams initParams = super.createInitParams(context);
            ncc.addAsSeparateChannels(
                    new WrapStackAsTimeSequenceStore(initParams.getImage().stacks()), 0);

            super.outputSharedObjects(initParams, context);

        } catch (CreateException | OutputWriteFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    public void processInputImage(
            MultiInput input,
            BoundIOContext context,
            OperationWithInitParams<ImageInitParams> operation)
            throws OperationFailedException {
        try {
            MPPInitParams initParams = super.createInitParams(input, context);

            operation.process(initParams.getImage());

            super.outputSharedObjects(initParams, context);

        } catch (CreateException | OutputWriteFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    public void processInputMPP(
            MultiInput input,
            BoundIOContext context,
            OperationWithInitParams<MPPInitParams> operation)
            throws OperationFailedException {
        try {
            MPPInitParams initParams = super.createInitParams(input, context);

            operation.process(initParams);

            super.outputSharedObjects(initParams, context);

        } catch (CreateException | OutputWriteFailedException e) {
            throw new OperationFailedException(e);
        }
    }
}
