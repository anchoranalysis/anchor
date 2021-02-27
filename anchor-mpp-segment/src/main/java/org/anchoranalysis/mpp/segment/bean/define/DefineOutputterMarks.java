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

import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.experiment.io.InitializationContext;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
import org.anchoranalysis.image.core.stack.time.WrapStackAsTimeSequenceStore;
import org.anchoranalysis.image.io.channel.input.series.NamedChannelsForSeries;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.mpp.bean.init.MarksInitialization;
import org.anchoranalysis.mpp.io.input.MultiInput;

public class DefineOutputterMarks extends DefineOutputter {

    @FunctionalInterface
    public interface OperationWithInitialization<T> {
        void process(T initialization) throws OperationFailedException;
    }

    public void processInput(NamedChannelsForSeries input, InitializationContext context)
            throws OperationFailedException {
        try {
            MarksInitialization initialization = super.createInitialization(context);
            input.addAsSeparateChannels(
                    new WrapStackAsTimeSequenceStore(initialization.getImage().stacks()), 0);

            super.outputSharedObjects(initialization, context.getOutputter().getChecked());

        } catch (CreateException | OutputWriteFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    public void processInputImage(
            MultiInput input,
            InitializationContext context,
            OperationWithInitialization<ImageInitialization> operation)
            throws OperationFailedException {
        try {
            MarksInitialization initialization = super.createInitialization(input, context);

            operation.process(initialization.getImage());

            super.outputSharedObjects(initialization, context.getOutputter().getChecked());

        } catch (CreateException | OutputWriteFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    public void processInputMPP(
            MultiInput input,
            InitializationContext context,
            OperationWithInitialization<MarksInitialization> operation)
            throws OperationFailedException {
        try {
            MarksInitialization initialization = super.createInitialization(input, context);

            operation.process(initialization);

            super.outputSharedObjects(initialization, context.getOutputter().getChecked());

        } catch (CreateException | OutputWriteFailedException e) {
            throw new OperationFailedException(e);
        }
    }
}
