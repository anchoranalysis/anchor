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
import java.util.Optional;
import org.anchoranalysis.io.input.InputContextParams;
import org.anchoranalysis.io.input.bean.InputManagerParams;
import org.anchoranalysis.io.input.file.FilesProviderException;

/**
 * Base class for implementations of {@link FilesProvider} which <b>do</b> have an associated
 * directory.
 *
 * @author Owen Feehan
 */
public abstract class FilesProviderWithDirectory extends FilesProvider {

    @Override
    public final Collection<File> create(InputManagerParams params) throws FilesProviderException {
        return matchingFilesForDirectory(getDirectoryAsPath(params.getInputContext()), params);
    }

    @Override
    public Optional<Path> rootDirectory(InputContextParams inputContext) {
        return Optional.of(getDirectoryAsPath(inputContext));
    }

    public abstract Path getDirectoryAsPath(InputContextParams inputContext);

    public abstract Collection<File> matchingFilesForDirectory(
            Path directory, InputManagerParams params) throws FilesProviderException;

    /** Like getDirectory as Path but converts any relative path to absolute one */
    public Path getDirectoryAsPathEnsureAbsolute(InputContextParams inputContext) {
        return makeAbsolutePathIfNecessary(getDirectoryAsPath(inputContext));
    }

    /**
     * If path is absolute, it's returned as-is If path is relative, and the 'makeAbsolute' option
     * is activated, it's added to the localizedPath
     *
     * @param path
     * @return
     */
    private Path makeAbsolutePathIfNecessary(Path path) {
        if (path.isAbsolute()) {
            return path;
        } else {
            Path parent = getLocalPath().getParent();
            return parent.resolve(path);
        }
    }
}
