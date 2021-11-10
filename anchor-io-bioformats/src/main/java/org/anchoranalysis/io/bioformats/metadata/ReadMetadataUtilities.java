package org.anchoranalysis.io.bioformats.metadata;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.BiFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Reads various image properties from tags in {@link Metadata}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadMetadataUtilities {

    /**
     * Reads metadata, if it exists, from an image.
     *
     * @param path the path to the image.
     * @return the metadata, if it exists.
     * @throws ImageIOException if something goes wrong processing the metadata.
     */
    public static Optional<Metadata> readMetadata(Path path) throws ImageIOException {
        try {
            return Optional.ofNullable(ImageMetadataReader.readMetadata(path.toFile()));
        } catch (ImageProcessingException | IOException e) {
            throw new ImageIOException("Failed to read metadata for " + path, e);
        }
    }

    /**
     * Finds the first {@link Directory} in the metadata with type {@code directoryType} and whose
     * name is equal to {@code directoryName}.
     *
     * @param <T> directory-type to find
     * @param metadata the metadata to read from.
     * @param directoryType class corresponding to {@code T}.
     * @param directoryName the name of the directory, which must match, case insensitive.
     * @return the first directory that matches, if it exists.
     */
    public static <T extends Directory> Optional<Directory> findDirectoryWithName(
            Metadata metadata, Class<T> directoryType, String directoryName) {
        for (T directory : metadata.getDirectoriesOfType(directoryType)) {
            if (directory.getName().equalsIgnoreCase(directoryName)) {
                return Optional.of(directory);
            }
        }
        return Optional.empty();
    }

    /**
     * Reads a metadata entry of type {@code int} from the first directory of type {@code
     * directoryType}.
     *
     * @param <T> directory-type to find
     * @param metadata the metadata to read from.
     * @param directoryType class corresponding to {@code T}.
     * @param tag a unique identifier from the metadata-extractor library identifying which tag to
     *     read.
     * @return the value of the tag, or {@link Optional#empty()} if it does not exist.
     */
    public static <T extends Directory> Optional<Integer> readInt(
            Metadata metadata, Class<T> directoryType, int tag) {

        Directory directory = metadata.getFirstDirectoryOfType(directoryType);
        return readInt(directory, tag);
    }

    /**
     * Reads a metadata entry of type {@code Date} from the first directory of type {@code
     * directoryType}.
     *
     * <p>If no timezone offset is specified in the metadata, the current time-zone is used.
     *
     * @param <T> directory-type to find
     * @param metadata the metadata to read from.
     * @param directoryType class corresponding to {@code T}.
     * @param tagsAcqusitionDate unique identifiers from the metadata-extractor library identifying
     *     which tag(s) to read (in order). Once a single tag is found, no further tags are tried.
     * @param tagsTimezoneOffset similar to {@code tagsAcqusitionDate} but instead identifies a
     *     time-zone offset.
     * @return the value of the tag, or {@link Optional#empty()} if it does not exist.
     */
    public static <T extends Directory> Optional<ZonedDateTime> readDate(
            Metadata metadata,
            Class<T> directoryType,
            int[] tagsAcqusitionDate,
            int[] tagsTimezoneOffset) {
        Directory directory = metadata.getFirstDirectoryOfType(directoryType);

        Optional<Date> date =
                readTagsUntilPresent(
                        directory, tagsAcqusitionDate, ReadMetadataUtilities::readDate);

        // Map to a time-zone
        return date.map(
                dateUnzoned ->
                        ZonedDateTime.ofInstant(
                                dateUnzoned.toInstant(),
                                timeZoneOffset(directory, tagsTimezoneOffset)));
    }

    private static ZoneId timeZoneOffset(Directory directory, int[] tagsTimezoneOffset) {
        Optional<Integer> zoneOffset =
                readTagsUntilPresent(directory, tagsTimezoneOffset, ReadMetadataUtilities::readInt);
        if (zoneOffset.isPresent()) {
            return ZoneId.ofOffset("UTC", ZoneOffset.ofHours(zoneOffset.get()));
        } else {
            return ZoneId.systemDefault();
        }
    }

    /**
     * Reads a metadata entry of type {@code int} from a {@link Directory}.
     *
     * @param directory the directory to read from.
     * @param tag a unique identifier from the metadata-extractor library identifying which tag to
     *     read.
     * @return the value of the tag, or {@link Optional#empty()} if it does not exist.
     */
    public static Optional<Integer> readInt(Directory directory, int tag) {
        return readTag(directory, tag, (dir, tagType) -> dir.getInteger(tagType));
    }

    /**
     * Reads a metadata entry of type {@code Date} from a {@link Directory}.
     *
     * @param directory the directory to read from.
     * @param tag a unique identifier from the metadata-extractor library identifying which tag to
     *     read.
     * @return the value of the tag, or {@link Optional#empty()} if it does not exist.
     */
    public static Optional<Date> readDate(Directory directory, int tag) {
        return readTag(
                directory, tag, (dir, tagType) -> dir.getDate(tagType, TimeZone.getDefault()));
    }

    /** Reads a metadata entry of type {@code T} from a {@link Directory}. */
    private static <T> Optional<T> readTag(
            Directory directory, int tag, BiFunction<Directory, Integer, T> extractTag) {

        // Search for a width and height directly in the EXIF
        // It is assumed this
        if (directory != null && directory.containsTag(tag)) {
            return Optional.of(extractTag.apply(directory, tag));
        } else {
            return Optional.empty();
        }
    }

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
    public static <T extends Directory> Optional<Extent> readFromWidthHeightTags(
            Metadata metadata, Class<T> directoryType, int tagWidth, int tagHeight)
            throws ImageIOException {

        Directory directory = metadata.getFirstDirectoryOfType(directoryType);

        // Search for a width and height directly in the EXIF
        // It is assumed this
        if (directory != null) {
            return readFromWidthHeightTags(directory, tagWidth, tagHeight);
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
    public static Optional<Extent> readFromWidthHeightTags(
            Directory directory, int tagWidth, int tagHeight) throws ImageIOException {

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

    /** Iteratively reads tags until the first present value is encountered. */
    private static <T> Optional<T> readTagsUntilPresent(
            Directory directory, int[] tags, BiFunction<Directory, Integer, Optional<T>> extract) {
        for (int tag : tags) {
            Optional<T> extractedValue = extract.apply(directory, tag);
            if (extractedValue.isPresent()) {
                return extractedValue;
            }
        }
        return Optional.empty();
    }
}
