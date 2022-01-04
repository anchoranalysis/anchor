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

package org.anchoranalysis.image.io.bean.stack.combine;

import java.util.List;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.spatial.arrange.StackArranger;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributes;
import org.anchoranalysis.image.io.stack.output.generator.RasterGenerator;
import org.anchoranalysis.image.io.stack.output.generator.RasterGeneratorSelectFormat;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

@AllArgsConstructor
class CombineGenerator<T> extends RasterGeneratorSelectFormat<T> {

    private final StackArranger arrangeRaster;
    private final List<RasterGenerator<T>> generators;

    @Override
    public Stack transform(T element) throws OutputWriteFailedException {

        List<RGBStack> generated = generateAll(element);

        try {
            // We get an image-stack for each generator
            //
            // Then we tile them
            //
            // We assume iterable generators always produce images of the same size
            //   and base our measurements on the first call to generate
            return arrangeRaster.combine(generated).asStack();

        } catch (ArrangeStackException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public StackWriteAttributes guaranteedImageAttributes() {
        return generators // NOSONAR
                .stream()
                .map(RasterGenerator::guaranteedImageAttributes)
                .reduce(StackWriteAttributes::and)
                .get();
    }

    private List<RGBStack> generateAll(T element) throws OutputWriteFailedException {
        return FunctionalList.mapToList(
                generators,
                OutputWriteFailedException.class,
                generator -> new RGBStack(generator.transform(element)));
    }
}
