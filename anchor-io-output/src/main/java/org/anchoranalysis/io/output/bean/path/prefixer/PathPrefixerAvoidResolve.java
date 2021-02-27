/*-
 * #%L
 * anchor-plugin-io
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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.output.path.prefixer.DirectoryWithPrefix;
import org.anchoranalysis.io.output.path.prefixer.NamedPath;
import org.anchoranalysis.io.output.path.prefixer.PathPrefixerContext;
import org.anchoranalysis.io.output.path.prefixer.PathPrefixerException;

/**
 * A file-path-resolver that adds additional methods that perform the same function but output a
 * relative-path rather than an absolute path after fully resolving paths
 *
 * <p>This is useful for combining with the MultiRootedFilePathPrefixer. This results in
 * relative-paths keeping the same root, as when passed in
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public abstract class PathPrefixerAvoidResolve extends PathPrefixer {

    // START BEAN PROPERTIES
    /**
     * A string prefixed to each output-path.
     *
     * <p>This is typically a directory which to output the experiment-directory and files, but it
     * may also be a combination of a directory and a file-name prefix.
     *
     * <p>If empty, first the bean will try to use any output-directory set in the input context if
     * it exists, or otherwise use the system temp directory.
     */
    @BeanField @AllowEmpty @Getter @Setter private String prefix = "";
    // END BEAN PROPERTIES

    // Caches the calculation
    private Path resolvedRoot = null;

    protected PathPrefixerAvoidResolve(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public DirectoryWithPrefix rootDirectoryPrefix(String expName, PathPrefixerContext context)
            throws PathPrefixerException {
        return new DirectoryWithPrefix(resolveExperimentAbsoluteRootOut(expName, context));
    }

    @Override
    public DirectoryWithPrefix outFilePrefix(
            NamedPath path, String expName, PathPrefixerContext context)
            throws PathPrefixerException {

        Path root = resolveExperimentAbsoluteRootOut(expName, context);
        return outFilePrefixFromPath(path, root, context);
    }

    /**
     * Provides a prefix that becomes the root-folder. It avoids resolving relative-paths.
     *
     * <p>This is an alternative method to rootDirectoryPrefix that avoids resolving the out-path
     * prefix against the file system
     *
     * @param experimentIdentifier an identifier for the experiment
     * @return a prefixer
     */
    public DirectoryWithPrefix rootDirectoryPrefixAvoidResolve(String experimentIdentifier) {
        String folder = getPrefix() + File.separator + experimentIdentifier + File.separator;
        return new DirectoryWithPrefix(Paths.get(folder));
    }

    /**
     * Provides a prefix which can be prepended to all output files. It avoids resolving
     * relative-paths.
     *
     * @param path an input-path to match against
     * @param experimentIdentifier an identifier for the experiment
     * @return a prefixer
     * @throws PathPrefixerException
     */
    public DirectoryWithPrefix outFilePrefixAvoidResolve(
            NamedPath path, String experimentIdentifier, PathPrefixerContext context)
            throws PathPrefixerException {
        return outFilePrefixFromPath(
                path,
                rootDirectoryPrefixAvoidResolve(experimentIdentifier).getDirectory(),
                context);
    }

    /**
     * Determines the out-file prefix from a path
     *
     * @param path path to calculate prefix from with associated descriptive-name
     * @param root root of prefix
     * @param context the path-prefixer context.
     * @return folder/filename for prefixing
     */
    public abstract DirectoryWithPrefix outFilePrefixFromPath(
            NamedPath path, Path root, PathPrefixerContext context) throws PathPrefixerException;

    /** The root of the experiment for outputting files */
    private Path resolveExperimentAbsoluteRootOut(
            String experimentName, PathPrefixerContext context) {

        if (resolvedRoot == null) {
            resolvedRoot = selectResolvedPath(context).resolve(experimentName);
        }
        return resolvedRoot;
    }

    private Path selectResolvedPath(PathPrefixerContext context) {
        if (!prefix.isEmpty()) {
            return resolvePath(prefix);
        } else {
            // If there's an prefix specified, then use it, otherwise a temporary directory.
            return context.getPrefixer()
                    .getOutputDirectory()
                    .orElseGet(PathPrefixerAvoidResolve::temporaryDirectory);
        }
    }

    private static Path temporaryDirectory() {
        return Paths.get(System.getProperty("java.io.tmpdir"));
    }
}
