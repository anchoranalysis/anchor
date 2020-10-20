/*-
 * #%L
 * anchor-experiment
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
package org.anchoranalysis.experiment.bean.task;

import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.system.path.PathDifference;
import org.anchoranalysis.core.system.path.PathDifferenceException;
import org.anchoranalysis.io.manifest.Manifest;
import org.anchoranalysis.io.manifest.ManifestDirectoryDescription;
import org.anchoranalysis.io.manifest.directory.JobRootDirectory;
import org.anchoranalysis.io.manifest.sequencetype.StringsWithoutOrder;
import org.anchoranalysis.io.output.path.DirectoryWithPrefix;
import org.anchoranalysis.io.output.path.FilePathPrefixerContext;
import org.anchoranalysis.io.output.path.NamedPath;
import org.anchoranalysis.io.output.path.PathPrefixer;
import org.anchoranalysis.io.output.path.PathPrefixerException;

@RequiredArgsConstructor
class PrefixForInput {

    private static final ManifestDirectoryDescription MANIFEST_DIRECTORY_ROOT =
            new ManifestDirectoryDescription("root", "experiment", new StringsWithoutOrder());

    private final PathPrefixer prefixer;

    private final FilePathPrefixerContext context;

    /**
     * The prefix to use for outputs pertaining to a particular file.
     *
     * @param path the path from which a prefix is derived
     * @param experimentIdentifier
     * @param experimentalManifest
     * @return
     * @throws PathPrefixerException
     */
    public DirectoryWithPrefix prefixForFile(
            NamedPath path, String experimentIdentifier, Optional<Manifest> experimentalManifest)
            throws PathPrefixerException {

        // Calculate a prefix from the incoming file, and create a file path generator
        DirectoryWithPrefix prefix = prefixer.outFilePrefix(path, experimentIdentifier, context);

        PathDifference difference =
                differenceFromPrefixer(experimentIdentifier, prefix.getCombined());

        experimentalManifest.ifPresent(
                recorder -> writeRootFolderInManifest(recorder, difference.combined()));

        return prefix;
    }

    private PathDifference differenceFromPrefixer(String experimentIdentifier, Path combinedPrefix)
            throws PathPrefixerException {
        try {
            return PathDifference.differenceFrom(
                    prefixer.rootFolderPrefix(experimentIdentifier, context).getCombined(),
                    combinedPrefix);
        } catch (PathDifferenceException e) {
            throw new PathPrefixerException(e);
        }
    }

    private static void writeRootFolderInManifest(Manifest manifestRecorder, Path rootPath) {
        manifestRecorder
                .getRootFolder()
                .recordSubdirectoryCreated(
                        rootPath, MANIFEST_DIRECTORY_ROOT, new JobRootDirectory());
    }
}
