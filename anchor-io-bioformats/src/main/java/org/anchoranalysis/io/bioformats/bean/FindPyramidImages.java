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
import java.util.TreeMap;
import java.util.stream.Stream;
import loci.formats.IFormatReader;
import loci.formats.meta.IMetadata;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.collection.StringSetTrie;
import org.anchoranalysis.image.io.ImageIOException;

/**
 * Finds images whose names suggest they belong as part of a pyramidal image structure.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class FindPyramidImages {

    /** Names of channels that are excluded as being pyramidal. */
    private static final StringSetTrie IMAGE_NAMES_TO_EXCLUDE =
            new StringSetTrie(Stream.of("thumbnail", "overview", "label", "macro"));

    /**
     * Builds a map of the name of each image to its series index, excluding any names that don't
     * seem like being part of a pyramid.
     *
     * @param reader the reader whose images across series are considered.
     * @return a newly created map from image-name to series index with the image - excluding any
     *     images as per above rules.
     * @throws ImageIOException if a writer cannot be created successfully.
     */
    public static Map<String, Integer> findImagesExcluding(IFormatReader reader)
            throws ImageIOException {
        IMetadata metadata = (IMetadata) reader.getMetadataStore();
        if (reader.getSeriesCount() != metadata.getImageCount()) {
            throw new ImageIOException(
                    String.format(
                            "Illegal state cannot continue as the Bioformat reader's series count (%d) is not identical the metadata account (%d)",
                            reader.getSeriesCount(), metadata.getImageCount()));
        }

        Map<String, Integer> map = new TreeMap<>();
        for (int series = 0; series < metadata.getImageCount(); series++) {
            if (!isImageThumbnail(reader, metadata, series)) {
                map.put(metadata.getImageName(series), series);
            }
        }
        return map;
    }

    /**
     * Does a particular image look like a thumbnail or some additional iamge (that is independent
     * of the main image).
     *
     * <p>A side-effect after calling this function, is that {@code reader} becomes set to {@code
     * series}.
     */
    private static boolean isImageThumbnail(IFormatReader reader, IMetadata metadata, int series) {
        reader.setSeries(series);
        if (reader.isThumbnailSeries()) {
            return true;
        } else {
            return reader.getResolutionCount() == 1
                    && IMAGE_NAMES_TO_EXCLUDE.contains(metadata.getImageName(series));
        }
    }
}
