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

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributes;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributesFactory;
import org.anchoranalysis.io.generator.SingleFileTypeGenerator;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Delegates a {@link RasterGeneratorSelectFormat} to a {@code SingleFileTypeGenerator<T,
 * DisplayStack>}.
 *
 * @author Owen Feehan
 * @param <T> element-type
 */
@RequiredArgsConstructor
public class RasterGeneratorDelegateToDisplayStack<T> extends RasterGeneratorSelectFormat<T> {

    // START REQUIRED ARGUMENTS
    private final SingleFileTypeGenerator<T, DisplayStack> delegate;

    /**
     * Whether the raster image that is produced is RGB or not.
     *
     * <p>RGB-A images should never be produced.
     */
    private final boolean rgb;

    // START END ARGUMENTS

    @Override
    public Stack transform(T element) throws OutputWriteFailedException {
        return delegate.transform(element).deriveStack(false);
    }

    @Override
    public StackWriteAttributes guaranteedImageAttributes() {
        return StackWriteAttributesFactory.maybeRGBWithoutAlpha(rgb);
    }
}
