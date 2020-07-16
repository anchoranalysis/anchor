/*-
 * #%L
 * anchor-io-generator
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

package org.anchoranalysis.io.generator.sequence;

import java.util.Collection;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class GeneratorSequenceIncrementalCollection<T, C>
        implements GeneratorSequenceIncremental<T> {

    private IterableObjectGenerator<T, C> iterableGenerator;
    private Collection<C> collection;

    public GeneratorSequenceIncrementalCollection(
            Collection<C> collection, IterableObjectGenerator<T, C> iterableGenerator) {
        super();

        this.collection = collection;
        this.iterableGenerator = iterableGenerator;
    }

    @Override
    public void add(T element) throws OutputWriteFailedException {

        try {
            iterableGenerator.setIterableElement(element);

            C generatedElement = iterableGenerator.getGenerator().generate();
            collection.add(generatedElement);
        } catch (SetOperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public void start() throws OutputWriteFailedException {
        iterableGenerator.start();
    }

    @Override
    public void end() throws OutputWriteFailedException {
        iterableGenerator.end();
    }
}
