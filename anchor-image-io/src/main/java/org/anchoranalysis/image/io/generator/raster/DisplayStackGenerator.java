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

package org.anchoranalysis.image.io.generator.raster;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.Stack;

/**
 * Writes a display-stack to the filesystem.
 *
 * @author Owen Feehan
 */
public class DisplayStackGenerator extends RasterGeneratorDelegateToRaster<Stack, DisplayStack> {

    /**
     * Creates the generator.
     *
     * @param manifestFunction function-stored in manifest for this generator
     * @param always2D if true, a stack is guaranteed always to be 2D (i.e. have only one z-slice).
     *     If false, it may be 2D or 3D.
     */
    public DisplayStackGenerator(String manifestFunction, boolean always2D) {
        super(new StackGenerator(manifestFunction, always2D));
    }

    @Override
    protected Stack convertBeforeAssign(DisplayStack element) throws OperationFailedException {
        return element.deriveStack(false);
    }

    @Override
    protected Stack convertBeforeTransform(Stack stack) {
        return stack;
    }
}
