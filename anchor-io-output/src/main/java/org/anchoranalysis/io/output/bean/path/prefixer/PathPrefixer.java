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

package org.anchoranalysis.io.output.bean.path.prefixer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.exception.BeanStrangeException;
import org.anchoranalysis.io.output.path.prefixer.DirectoryWithPrefix;
import org.anchoranalysis.io.output.path.prefixer.NamedPath;
import org.anchoranalysis.io.output.path.prefixer.PathPrefixerContext;
import org.anchoranalysis.io.output.path.prefixer.PathPrefixerException;

/**
 * Base classes for methods to determine an output directory and associated file prefix.
 *
 * <p>This is derived from an identifier for an experiment, and input paths.
 *
 * @author Owen Feehan
 */
public abstract class PathPrefixer extends AnchorBean<PathPrefixer> {

    /**
     * Provides a prefix which can be prepended to all output files. The prefix should be an
     * absolute path.
     *
     * @param path an input to derive a prefix from
     * @param experimentIdentifier if defined, an identifier for the experiment, to be included in
     *     the directory root.
     * @param context the context in which prefixing of paths occurs.
     * @return a directory with an associated prefix.
     * @throws PathPrefixerException if unable to successfully determine a prefix.
     */
    public abstract DirectoryWithPrefix outFilePrefix(
            NamedPath path, Optional<String> experimentIdentifier, PathPrefixerContext context)
            throws PathPrefixerException;

    /**
     * Provides a prefix that becomes the root-folder. The prefix should be an absolute path.
     *
     * @param experimentIdentifier if defined, an identifier for the experiment, to be included in
     *     the directory root.
     * @param context the context in which prefixing of paths occurs.
     * @return a directory with an associated prefix.
     * @throws PathPrefixerException if unable to successfully determine a prefix.
     */
    public abstract DirectoryWithPrefix rootDirectoryPrefix(
            Optional<String> experimentIdentifier, PathPrefixerContext context)
            throws PathPrefixerException;

    /**
     * Like {@link #resolvePath(Path)} but accepts a String as the path.
     *
     * @param pathToResolve input-path that is relative.
     * @return the converted path (relative to the localizedPath of the current file)
     */
    protected Path resolvePath(String pathToResolve) {
        return resolvePath(Paths.get(pathToResolve));
    }

    /**
     * Converts a relative-path to an absolute-path (relative to the file-path associated with this
     * current bean)
     *
     * <p>If there is no file-path associated with the current bean, then we throw an error if it is
     * a relative path, or otherwise it remains unchanged
     *
     * <p>If the pathToResolve is already absolute, then we return it as-is
     *
     * @param pathToResolve input-path that is relative.
     * @return the converted path (relative to the localizedPath of the current file)
     */
    protected Path resolvePath(Path pathToResolve) {

        if (pathToResolve.isAbsolute()) {
            // It's okay we it's absolute path
            return pathToResolve;
        }

        Path localPath = getLocalPath();

        if (localPath != null) {
            assert !pathToResolve.isAbsolute();
            assert localPath.isAbsolute();
            Path parent = localPath.getParent();

            return parent.resolve(pathToResolve).normalize();
        } else {
            // We have a relative path
            throw new BeanStrangeException(
                    String.format(
                            "Cannot resolve relative-path: %s as there is no localPath for this bean",
                            pathToResolve));
        }
    }
}
