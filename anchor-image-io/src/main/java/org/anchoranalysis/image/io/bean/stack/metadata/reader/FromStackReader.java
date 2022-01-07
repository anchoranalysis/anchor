/*-
 * #%L
 * anchor-plugin-io
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
package org.anchoranalysis.image.io.bean.stack.metadata.reader;

import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.DefaultInstance;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.core.time.OperationContext;
import org.anchoranalysis.image.core.stack.ImageMetadata;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.reader.StackReader;
import org.anchoranalysis.image.io.stack.input.OpenedImageFile;

/**
 * Uses a {@link StackReader} to read the image-metadata.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class FromStackReader extends ImageMetadataReader {

    // START BEAN PROPERTIES
    /**
     * The {@link StackReader} to use to read the image metadata.
     *
     * <p>If not specified, the default {@link StackReader} is used. Note that this bean does not
     * allow the default -instance to be passed by {@link DefaultInstance}, as the bean often needs
     * to be specified in the {@code defaultBeans.xml} configuration file, where it is not already
     * known.
     */
    @BeanField @Getter @Setter @OptionalBean private StackReader stackReader;

    /** The series to open to the read the metadata (zero-indexed). */
    @BeanField @Getter @Setter private int seriesIndex = 0;
    // END BEAN PROPERTIES

    /**
     * Create from a {@link StackReader}.
     *
     * @param stackReader the reader.
     */
    public FromStackReader(StackReader stackReader) {
        this.stackReader = stackReader;
    }

    @Override
    public ImageMetadata openFile(
            Path path, StackReader defaultStackReader, OperationContext context)
            throws ImageIOException {

        StackReader selectedReader = Optional.ofNullable(stackReader).orElse(defaultStackReader);

        try (OpenedImageFile file =
                selectedReader.openFile(path, context.getExecutionTimeRecorder())) {
            return file.metadata(seriesIndex, context.getLogger());
        }
    }
}
