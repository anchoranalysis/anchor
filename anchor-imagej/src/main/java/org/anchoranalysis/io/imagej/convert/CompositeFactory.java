/*-
 * #%L
 * anchor-imagej
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
package org.anchoranalysis.io.imagej.convert;

import com.google.common.base.Preconditions;
import ij.CompositeImage;
import ij.ImagePlus;
import ij.ImageStack;
import lombok.RequiredArgsConstructor;

/**
 * Creates a {@link ImagePlus} from a {@link ImageStack} that is either composite or non-composite.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
class CompositeFactory {

    private static final String IMAGEJ_IMAGE_NAME = "imagename";

    // START REQUIRED ARGUMENTS
    /** Stack from which a {@link ImagePlus} is derived. */
    private final ImageStack stack;

    /** Number of slices in {@code stack}. */
    private final int numberSlices;

    /** Number of frames in {@code stack}. */
    private final int numberFrames;
    // END REQUIRED ARGUMENTS

    /**
     * Creates an image-plus either composite or non-composite based on a flag
     *
     * @param numberChannels number of channels to be created if composite, otherwise only a single
     *     composite channel exists
     * @param makeComposite the flag, if true composite is created or otherwise non-composite
     * @return a newly-created {@link ImagePlus} either composite or non-composite
     */
    public ImagePlus create(int numberChannels, boolean makeComposite) {
        // If we're making an RGB then we need to convert our stack
        if (makeComposite) {
            return createComposite(numberChannels);
        } else {
            return createNonComposite(1);
        }
    }

    private ImagePlus createComposite(int numberChannels) {
        ImagePlus nonComposite = createNonComposite(numberChannels);
        Preconditions.checkArgument(nonComposite.getNSlices() == numberSlices);
        ImagePlus composite = new CompositeImage(nonComposite, CompositeImage.COLOR);

        // The Composite image sometimes sets these wrong, so we force the correct dimensionality
        composite.setDimensions(numberChannels, numberSlices, numberFrames);
        return composite;
    }

    private ImagePlus createNonComposite(int numberChannelsForComposite) {
        ImagePlus imp = new ImagePlus();
        imp.setStack(stack, numberChannelsForComposite, numberSlices, numberFrames);
        imp.setTitle(IMAGEJ_IMAGE_NAME);
        return imp;
    }
}
