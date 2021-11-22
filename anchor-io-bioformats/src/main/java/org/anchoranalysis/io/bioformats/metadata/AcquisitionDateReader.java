/*-
 * #%L
 * anchor-io-bioformats
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.image.io.ImageIOException;

/**
 * Reads the <b>image acquisition-date</b>. from EXIF metadata, if specified in an image-file.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AcquisitionDateReader {

    /**
     * The tags tried in order in the EXIF data to read the acqusition-time.
     *
     * <p>Please see <a
     * href="https://www.quora.com/What-is-the-difference-between-Date-and-Time-Original-and-Date-and-Time-in-the-EXIF-data-output">this
     * quora post</a> for information about EXIF tags relating to date.
     */
    private static final int[] ACQUSITION_TIME_TAGS =
            new int[] {
                ExifDirectoryBase.TAG_DATETIME,
                ExifDirectoryBase.TAG_DATETIME_ORIGINAL,
                ExifDirectoryBase.TAG_DATETIME_DIGITIZED
            };

    /**
     * The tags tried in order in the EXIF data to read the acqusition-time zone.
     *
     * <p>A timezone is not specified, by default, with EXIF data for times. But some
     * quasi-unofficial tags apparently specify it.
     */
    private static final int[] TIMEZONE_TAGS =
            new int[] {
                ExifDirectoryBase.TAG_TIME_ZONE_OFFSET,
                ExifDirectoryBase.TAG_TIME_ZONE_OFFSET_TIFF_EP
            };

    /**
     * Reads an image acquisition-date from a file identified by path, based on the present of EXIF
     * data.
     *
     * <p>Only files with extensions matching JPEG or TIFF are checked.
     *
     * <p>Three different EXIF tags are tried in a particular order, and the first existing tag is
     * treated as the acqusition date:
     *
     * <ol>
     *   <li>{@link ExifDirectoryBase#TAG_DATETIME}
     *   <li>{@link ExifDirectoryBase#TAG_DATETIME_ORIGINAL}
     *   <li>{@link ExifDirectoryBase#TAG_DATETIME_DIGITIZED}
     * </ol>
     *
     * @param path the path to the image.
     * @return the acquisition date, if it exists.
     * @throws ImageIOException if the metadata is errored (but not if metadata is absent).
     */
    public static Optional<ZonedDateTime> readAcquisitionDate(Path path) throws ImageIOException {
        if (ImageFileFormat.JPEG.matches(path) || ImageFileFormat.TIFF.matches(path)) {
            Optional<Metadata> metadata = ReadMetadataUtilities.readMetadata(path);
            return metadata.flatMap(AcquisitionDateReader::readAcquisitionDate);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Reads an image acquisition-date from metadata, based on the present of EXIF data.
     *
     * <p>The same procedure is used as in {@link #readAcquisitionDate(Path)}.
     *
     * @param metadata the metadata.
     * @return the acquisition date, if it exists.
     */
    public static Optional<ZonedDateTime> readAcquisitionDate(Metadata metadata) {
        return ReadMetadataUtilities.readDate(
                metadata, ExifIFD0Directory.class, ACQUSITION_TIME_TAGS, TIMEZONE_TAGS);
    }
}
