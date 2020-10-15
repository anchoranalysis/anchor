/*-
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.io.input.bean.path.provider;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.io.input.InputContextParams;
import org.anchoranalysis.io.input.bean.InputManagerParams;
import org.anchoranalysis.io.input.bean.files.FilesProvider;
import org.anchoranalysis.io.input.files.FilesProviderException;

/**
 * Extracts a path from a {@link FilesProvider}.
 * 
 * <p>The {@link FilesProvider} must return exactly one file, otherwise an exception is thrown.
 * 
 * @author Owen Feehan
 *
 */
public class FromFiles extends FilePathProvider {

    // START BEAN PROPERTIES
    /**
     * A provider that should return exactly one {@link File} whose path is employed.
     */
    @BeanField @Getter @Setter private FilesProvider filesProvider;
    // END BEAN PROPERTIES

    @Override
    public Path create() throws CreateException {

        Collection<File> files;
        try {
            files =
                    filesProvider.create(
                            new InputManagerParams(
                                    new InputContextParams(),
                                    ProgressReporterNull.get(),
                                    getLogger()));
        } catch (FilesProviderException e) {
            throw new CreateException("Cannot find files", e);
        }

        if (files.size() != 1) {
            throw new CreateException(
                    String.format(
                            "filesProvider must return one file. Instead, it returned %d files.",
                            files.size()));
        }

        return files.iterator().next().toPath();
    }
}
