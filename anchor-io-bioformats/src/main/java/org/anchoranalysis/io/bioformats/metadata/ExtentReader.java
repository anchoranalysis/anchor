/*-
 * #%L
 * anchor-io-bioformats
 * %%
 * Copyright (C) 2010 - 2025 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
