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
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.io.input.InputContextParameters;
import org.anchoranalysis.io.input.bean.InputManager;
import org.anchoranalysis.io.input.bean.InputManagerParameters;
import org.anchoranalysis.io.input.file.FilesProviderException;

/**
 * Base class for implementations of {@link FilesProvider} which <b>do</b> have an associated
 * directory.
 *
 * @author Owen Feehan
 */
public abstract class FilesProviderWithDirectory extends FilesProvider {

    @Override
    public final List<File> create(InputManagerParameters parameters)
            throws FilesProviderException {
        return matchingFilesForDirectory(
                getDirectoryAsPath(parameters.getInputContext()), parameters);
    }

    @Override
    public Optional<Path> rootDirectory(InputContextParameters inputContext) {
        return Optional.of(getDirectoryAsPath(inputContext));
    }

    /**
     * The associated directory with the list of files.
     *
     * @param inputContext the input-context.
     * @return the associated directory, as a path.
     */
    public abstract Path getDirectoryAsPath(InputContextParameters inputContext);

    /**
     * The matching files for this provider that exist in a particular directory.
     *
     * <p>This directory may or may not be searched recursively, depending on implementation.
     *
     * @param directory the directory.
     * @param parameters parameters passed to an {@link InputManager} to generate input-objects.
     * @return a newly created list of matching files.
     * @throws FilesProviderException if the operation is unable to complete successfully.
     */
    public abstract List<File> matchingFilesForDirectory(
            Path directory, InputManagerParameters parameters) throws FilesProviderException;

    /**
     * Like {@link #getDirectoryAsPath} but converts any relative path to absolute one.
     *
     * @param inputContext the input-context.
     * @return an absolute path.
     */
    public Path getDirectoryAsPathEnsureAbsolute(InputContextParameters inputContext) {
        return makeAbsolutePathIfNecessary(getDirectoryAsPath(inputContext));
    }

    /**
     * If path is relative, it's joined to path associated with this particular bean.
     *
     * <p>If path is absolute, it is returned unchanged.
     *
     * @param path the path which may be relative or absolute.
     * @return a resolved path as per above if relative, or an unchanged path if absolute.
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
