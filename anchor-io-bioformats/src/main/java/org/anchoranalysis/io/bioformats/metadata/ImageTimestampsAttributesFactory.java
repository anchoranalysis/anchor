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

import java.io.IOException;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.core.stack.ImageFileAttributes;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.stack.input.ImageTimestampsAttributes;

/**
 * Derives {@link ImageTimestampsAttributes} from a file, additionally checking for EXIF metadata.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageTimestampsAttributesFactory {

    /**
     * Reads {@link ImageTimestampsAttributes} from a path, trying to infer the acquisition-date
     * from metadata headers.
     *
     * <p>For JPEG or TIFF files, EXIF data is searched for to find an acquisition-date.
     *
     * @param path the path.
     * @return newly created {@link ImageFileAttributes}.
     * @throws ImageIOException if {@link ImageTimestampsAttributes} cannot be established.
     */
    public static ImageTimestampsAttributes fromPath(Path path) throws ImageIOException {
        try {
            return new ImageTimestampsAttributes(
                    ImageFileAttributes.fromPath(path),
                    AcquisitionDateReader.readAcquisitionDate(path));
        } catch (IOException e) {
            throw new ImageIOException("Failed to read file-attributes for " + path, e);
        }
    }
}
