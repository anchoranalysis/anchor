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

package org.anchoranalysis.image.io;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
import org.anchoranalysis.image.core.dimensions.size.suggestion.ImageSizeSuggestion;
import org.anchoranalysis.io.output.outputter.InputOutputContext;

/**
 * Creates instances of {@link ImageInitialization} based upon an {@link InputOutputContext}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageInitializationFactory {

    /**
     * Create from an {@link InputOutputContext} - without any suggested size.
     *
     * @param context the context.
     * @return a newly created {@link ImageInitialization} based upon {@code context}.
     */
    public static ImageInitialization create(InputOutputContext context) {
        return create(context, Optional.empty());
    }

    /**
     * Create from an {@link InputOutputContext} - with a suggested size.
     *
     * @param context the context.
     * @param suggestedSize a suggested input on how to resize an image, if one is provided.
     * @return a newly created {@link ImageInitialization} based upon {@code context}.
     */
    public static ImageInitialization create(
            InputOutputContext context, Optional<ImageSizeSuggestion> suggestedSize) {
        SharedObjects sharedObjects = new SharedObjects(context.common());
        return new ImageInitialization(sharedObjects, suggestedSize);
    }
}
