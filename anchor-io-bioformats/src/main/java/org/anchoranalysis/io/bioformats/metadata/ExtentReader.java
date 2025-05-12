package org.anchoranalysis.io.bioformats.metadata;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import java.util.Optional;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.spatial.box.Extent;

/** Reads an {@link Extent} from the Metadata tags of a file. */
public class ExtentReader {

    /**
     * Reads two metadata entries, representing width and height, and use them to form a {@link
     * Extent}.
     *
     * <p>The first directory of type {@code directoryType} is used for the tags.
     *
     * @param <T> directory-type to find
     * @param metadata the metadata to read from.
     * @param directoryType class corresponding to {@code T}.
     * @param tagWidth a unique identifier from the metadata-extractor library identifying the
     *     <i>width</i> tag.
     * @param tagHeight a unique identifier from the metadata-extractor library identifying the
     *     <i>height</i> tag.
     * @return the value of the tag, or {@link Optional#empty()} if it does not exist.
     * @throws ImageIOException if the metadata is errored (but not if it is absent).
     */
    public static <T extends Directory> Optional<Extent> read(
            Metadata metadata, Class<T> directoryType, int tagWidth, int tagHeight)
            throws ImageIOException {

        Directory directory = metadata.getFirstDirectoryOfType(directoryType);

        // Search for a width and height directly in the EXIF
        // It is assumed this
        if (directory != null) {
            return read(directory, tagWidth, tagHeight);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Reads two metadata entries, representing width and height, and use them to form a {@link
     * Extent}.
     *
     * @param directory the directory to read tags from.
     * @param tagWidth a unique identifier from the metadata-extractor library identifying the
     *     <i>width</i> tag.
     * @param tagHeight a unique identifier from the metadata-extractor library identifying the
     *     <i>height</i> tag.
     * @return the value of the tag, or {@link Optional#empty()} if it does not exist.
     * @throws ImageIOException if the metadata is errored (but not if it is absent).
     */
    public static Optional<Extent> read(Directory directory, int tagWidth, int tagHeight)
            throws ImageIOException {

        if (directory.containsTag(tagWidth) && directory.containsTag(tagHeight)) {

            try {
                int width = directory.getInt(tagWidth);
                int height = directory.getInt(tagHeight);

                if (width == 0 || height == 0) {
                    throw new ImageIOException(
                            "A width or height of 0 was specified in metadata, which suggests a format is not supported e.g perhaps JPEGs with DNL markers.");
                }
                return Optional.of(new Extent(width, height));

            } catch (MetadataException e) {
                throw new ImageIOException("Image metadata exists in an invalid state.", e);
            }

        } else {
            return Optional.empty();
        }
    }
}
