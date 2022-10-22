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
