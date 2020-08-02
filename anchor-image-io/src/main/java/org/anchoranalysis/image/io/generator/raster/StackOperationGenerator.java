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

import java.util.Optional;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StackOperationGenerator extends RasterGenerator
        implements IterableObjectGenerator<Operation<Stack, OutputWriteFailedException>, Stack> {

    private Operation<Stack, OutputWriteFailedException> stackIn;
    private boolean padIfNec;
    private String manifestFunction;

    public StackOperationGenerator(boolean padIfNec, String manifestFunction) {
        super();
        this.padIfNec = padIfNec;
        this.manifestFunction = manifestFunction;
    }

    @Override
    public Stack generate() throws OutputWriteFailedException {
        assert (stackIn != null);
        return StackGenerator.generateStack(stackIn.doOperation(), padIfNec);
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", manifestFunction));
    }

    @Override
    public ObjectGenerator<Stack> getGenerator() {
        return this;
    }

    @Override
    public Operation<Stack, OutputWriteFailedException> getIterableElement() {
        return stackIn;
    }

    @Override
    public void setIterableElement(Operation<Stack, OutputWriteFailedException> element) {
        this.stackIn = element;
    }

    @Override
    public boolean isRGB() throws OutputWriteFailedException {
        return stackIn.doOperation().getNumberChannels() == 3
                || (stackIn.doOperation().getNumberChannels() == 2 && padIfNec);
    }
}
