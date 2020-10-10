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
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.io.rasterwriter.RasterWriteOptions;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Writes a stack to the filesystem.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class StackGenerator extends RasterGeneratorWithElement<Stack> {

    /**
     * Iff true, in the specific case of a 2-channel stack, an additional blank channel is added to
     * make it 3-channels.
     */
    private boolean padIfNecessary;

    /** Function stored in manifest for this generator. */
    private Optional<String> manifestFunction;

    /**
     * If true, a stack is guaranteed always to have only one z-slice. If false, it may be 2D or 3D.
     */
    private boolean always2D;

    /**
     * Creates a generator that performs no padding.
     *
     * @param manifestFunction manifestFunction function stored in manifest for this generator
     * @param always2D if true, a stack is guaranteed always to be 2D (i.e. have only one z-slice).
     *     If false, it may be 2D or 3D.
     */
    public StackGenerator(String manifestFunction, boolean always2D) {
        this(Optional.of(manifestFunction), always2D);
    }
    
    /**
     * Creates a generator that performs no padding.
     *
     * @param manifestFunction manifestFunction function stored in manifest for this generator
     * @param always2D if true, a stack is guaranteed always to be 2D (i.e. have only one z-slice).
     *     If false, it may be 2D or 3D.
     */
    public StackGenerator(Optional<String> manifestFunction, boolean always2D) {
        this(false, manifestFunction, always2D);
    }

    /**
     * Creates the generator from a stack, inferring whether all stacks will be 2D from this stack's
     * dimensions.
     *
     * @param padIfNecessary iff true, in the specific case of a 2-channel stack, an additional
     *     blank channel is added to make it 3-channels.
     * @param manifestFunction function stored in manifest for this generator.
     * @param stack the initial element
     */
    public StackGenerator(boolean padIfNecessary, String manifestFunction, Stack stack) {
        this(padIfNecessary, Optional.of(manifestFunction), stack.extent().z() == 1);
        assignElement(stack);
    }

    @Override
    public Stack transform() throws OutputWriteFailedException {
        Stack out = getElement().duplicateShallow();

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
    public Optional<ManifestDescription> createManifestDescription() {
        return manifestFunction.map( function -> new ManifestDescription("raster", function));
    }

    @Override
    public boolean isRGB() {
        int numberChannels = getElement().getNumberChannels();
        return numberChannels == 3 || (numberChannels == 2 && padIfNecessary);
    }

    @Override
    public RasterWriteOptions rasterWriteOptions() {
        return RasterWriteOptions.maybeRGB(isRGB(), always2D);
    }
}
