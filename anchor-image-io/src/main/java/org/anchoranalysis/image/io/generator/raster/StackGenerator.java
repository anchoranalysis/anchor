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
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StackGenerator extends RasterGeneratorWithElement<Stack> {

    private boolean padIfNecessary;
    private String manifestFunction;

    // Won't do any padding
    public StackGenerator(String manifestFunction) {
        this(false, manifestFunction);
    }

    // Notes pads the passed channel, would be better if it makes a new stack first
    public StackGenerator(boolean padIfNecessary, String manifestFunction, Stack stack) {
        super();
        this.padIfNecessary = padIfNecessary;
        this.manifestFunction = manifestFunction;
        assignElement(stack);
    }

    public static Stack generateStack(Stack stackIn, boolean padIfNec)
            throws OutputWriteFailedException {
        Stack stackOut = new Stack();

        try {
            for (int c = 0; c < stackIn.getNumberChannels(); c++) {
                stackOut.addChannel(stackIn.getChannel(c));
            }
        } catch (IncorrectImageSizeException e) {
            throw new OutputWriteFailedException(e);
        }

        try {
            if (padIfNec && stackOut.getNumberChannels() == 2) {
                stackOut.addBlankChannel();
            }
        } catch (OperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }

        return stackOut;
    }

    @Override
    public Stack transform() throws OutputWriteFailedException {
        return generateStack(getElement(), padIfNecessary);
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", manifestFunction));
    }

    @Override
    public boolean isRGB() {
        int numberChannels = getElement().getNumberChannels();
        return numberChannels == 3 || (numberChannels == 2 && padIfNecessary);
    }
}
