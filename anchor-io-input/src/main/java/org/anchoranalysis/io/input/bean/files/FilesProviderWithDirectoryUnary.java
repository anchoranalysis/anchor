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
package org.anchoranalysis.io.input.bean.files;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.input.InputContextParams;
import org.anchoranalysis.io.input.bean.InputManagerParams;
import org.anchoranalysis.io.input.files.FilesProviderException;

/**
 * Like {@link FilesProviderWithDirectory} but employs a unary operator on a call to an existing
 * {@link FilesProviderWithDirectory}.
 *
 * <p>It is assumed that the associated directory is not altered.
 *
 * @author Owen Feehan
 */
public abstract class FilesProviderWithDirectoryUnary extends FilesProviderWithDirectory {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private FilesProviderWithDirectory files;
    // END BEAN PROPERTIES

    @Override
    public Collection<File> matchingFilesForDirectory(Path directory, InputManagerParams params)
            throws FilesProviderException {
        return transform(files.matchingFilesForDirectory(directory, params));
    }

    @Override
    public Path getDirectoryAsPath(InputContextParams inputContext) {
        return files.getDirectoryAsPath(inputContext);
    }

    /**
     * Transform an existing collection of files.
     *
     * <p>Note that the incoming collection of files may be modified, and can no longer be used in
     * its original form after this method call.
     *
     * @param source the incoming files (which may be consumed and modified).
     * @return the transformed (outgoing) files.
     */
    protected abstract Collection<File> transform(Collection<File> source)
            throws FilesProviderException;
}
