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

package org.anchoranalysis.image.io.stack.output.generator;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributes;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributesFactory;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Writes a stack to the filesystem.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class StackGenerator extends RasterGeneratorSelectFormat<Stack> {

    /**
     * Iff true, in the specific case of a 2-channel stack, an additional blank channel is added to
     * make it 3-channels.
     */
    private boolean padIfNecessary;

    /** Properties of the stack that is being written used to guide the outputting. */
    private StackWriteAttributes writeOptions;

    /**
     * Creates a generator that performs no padding.
     *
     * @param always2D when true, this indicates that the stack is guaranteed to be always 2D.
     */
    public StackGenerator(boolean always2D) {
        this(false, always2D);
    }

    /**
     * Creates the generator from a stack, inferring whether all stacks will be 2D from this stack's
     * dimensions.
     *
     * @param padIfNecessary iff true, in the specific case of a 2-channel stack, an additional
     *     blank channel is added to make it 3-channels.
     * @param always2D when true, this indicates that the stack is guaranteed to be always 2D.
     */
    public StackGenerator(boolean padIfNecessary, boolean always2D) {
        this(padIfNecessary, StackWriteAttributesFactory.maybeAlways2D(always2D));
    }

    @Override
    public Stack transform(Stack element) throws OutputWriteFailedException {
        Stack out = element.duplicateShallow();

        try {
            if (padIfNecessary && out.getNumberChannels() == 2) {
                out.addBlankChannel();
            }
        } catch (OperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }

        return out;
    }

    @Override
    public StackWriteAttributes guaranteedImageAttributes() {
        return writeOptions;
    }
}
