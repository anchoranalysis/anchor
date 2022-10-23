/*-
 * #%L
 * anchor-io-bioformats
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.io.bioformats.bean;

import java.util.Map;
import java.util.Optional;
import loci.formats.IFormatReader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.OptionalFactory;
import org.anchoranalysis.image.core.stack.ImagePyramidMetadata;
import org.anchoranalysis.image.io.ImageIOException;

/**
 * Derives a {@link ImagePyramidMetadata} where possible from a {@link IFormatReader}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ImagePyramidMetadataFactory {

    /**
     * Attempts to derive {@link ImagePyramidMetadata} from a {@code reader}.
     *
     * @param reader a reader for a particular file, with a file-path-assigned, and with {@link
     *     IFormatReader#setFlattenedResolutions} has been called with the value {@code false} prior
     *     to this.
     * @return the derived {@link ImagePyramidMetadata} if at least one image-pyramid exists in the
     *     file, or {@link Optional#empty} if none exist.
     * @throws ImageIOException if the number of series returned by the reader does not equal the
     *     number listed in the metadata.
     */
    public static Optional<ImagePyramidMetadata> derivePyramidMetadata(IFormatReader reader)
            throws ImageIOException {
        if (reader.getSeriesCount() == 1) {
            // Only a single series, so what matters is the resolution-count
            return createPyramidWhenTwoOrMore(reader.getResolutionCount());
        } else {
            // Multiple series, so the next step is to determine if either:
            //     any of them is a pyramid (independently)
            //  or together a subset form a pyramid (which can happen with Bioformats with some
            // formats, like svs).
            Map<String, Integer> imageMap = FindPyramidImages.findImagesExcluding(reader);

            // For now just assume all images that don't looking like thumbnails or additional
            // images, form a separate level in the pyramid.
            int numberImages = imageMap.size();
            if (numberImages == 0) {
                // With zero images in the map, let's use the resolution count from the first series
                reader.setSeries(0);
                return createPyramidWhenTwoOrMore(reader.getResolutionCount());
            }
            if (numberImages == 1) {
                // With one image in the map, let's use the resolution count from that image
                reader.setSeries(imageMap.values().iterator().next());
                return createPyramidWhenTwoOrMore(reader.getResolutionCount());
            } else {
                // Otherwise consider all the images in the map as separate resolution levels
                return createPyramidWhenTwoOrMore(numberImages);
            }
        }
    }

    /**
     * Creates {@link ImagePyramidMetadata} when {@code count > 1} or otherwise {@link
     * Optional#empty()}.
     */
    private static Optional<ImagePyramidMetadata> createPyramidWhenTwoOrMore(int count) {
        return OptionalFactory.create(count > 1, () -> new ImagePyramidMetadata(count));
    }
}
