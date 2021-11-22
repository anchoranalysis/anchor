/*-
 * #%L
 * anchor-image-core
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
package org.anchoranalysis.image.core.stack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.system.path.ExtensionUtilities;

/**
 * Timestamps and other metadata associated with an image file-path, but not with the file's
 * contents.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class ImageFileAttributes {

    /** The path on the file-system. */
    private Path path;

    /** The <i>creation</i> timestamp on the file the image was loaded from. */
    @Getter private ZonedDateTime creationTime;

    /** The <i>last modified</i> timestamp on the file the image was loaded from. */
    @Getter private ZonedDateTime modificationTime;

    /**
     * Reads {@link ImageFileAttributes} from a path.
     *
     * @param path the path.
     * @return newly created {@link ImageFileAttributes}.
     * @throws IOException if the timestamps cannot be read.
     */
    public static ImageFileAttributes fromPath(Path path) throws IOException {
        BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
        return new ImageFileAttributes(
                path,
                convertToDate(attributes.creationTime()),
                convertToDate(attributes.lastModifiedTime()));
    }

    /**
     * The file extension of the file-path.
     *
     * @return the extension, always in lower-case.
     */
    public Optional<String> extension() {
        return ExtensionUtilities.extractExtension(path).map(String::toLowerCase);
    }

    /** Converts from a {@link FileTime} to a {@link Date}. */
    private static ZonedDateTime convertToDate(FileTime time) {
        return ZonedDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());
    }
}
