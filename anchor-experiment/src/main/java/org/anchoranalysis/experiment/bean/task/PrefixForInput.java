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
import org.anchoranalysis.io.bean.filepath.prefixer.FilePathPrefixer;
import org.anchoranalysis.io.bean.filepath.prefixer.NamedPath;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.error.FilePathPrefixerException;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefixerContext;
import org.anchoranalysis.io.filepath.prefixer.PathDifference;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.folder.ExperimentFileFolder;
import org.anchoranalysis.io.manifest.sequencetype.StringsWithoutOrder;

@RequiredArgsConstructor
class PrefixForInput {

    private static final ManifestFolderDescription MANIFEST_FOLDER_ROOT =
            new ManifestFolderDescription("root", "experiment", new StringsWithoutOrder());

    private final FilePathPrefixer prefixer;

    private final FilePathPrefixerContext context;

    /**
     * The prefix to use for outputs pertaining to a particular file.
     *
     * @param path the path from which a prefix is derived
     * @param experimentIdentifier
     * @param experimentalManifest
     * @return
     * @throws FilePathPrefixerException
     */
    public FilePathPrefix prefixForFile(
            NamedPath path,
            String experimentIdentifier,
            Optional<ManifestRecorder> experimentalManifest)
            throws FilePathPrefixerException {

        // Calculate a prefix from the incoming file, and create a file path generator
        FilePathPrefix prefix = prefixer.outFilePrefix(path, experimentIdentifier, context);

        PathDifference difference =
                differenceFromPrefixer(experimentIdentifier, prefix.getCombinedPrefix());

        experimentalManifest.ifPresent(
                recorder -> writeRootFolderInManifest(recorder, difference.combined()));

        return prefix;
    }

    private PathDifference differenceFromPrefixer(String experimentIdentifier, Path combinedPrefix)
            throws FilePathPrefixerException {
        try {
            return PathDifference.differenceFrom(
                    prefixer.rootFolderPrefix(experimentIdentifier, context).getCombinedPrefix(),
                    combinedPrefix);
        } catch (AnchorIOException e) {
            throw new FilePathPrefixerException(e);
        }
    }

    private static void writeRootFolderInManifest(
            ManifestRecorder manifestRecorder, Path rootPath) {
        manifestRecorder
                .getRootFolder()
                .writeFolder(rootPath, MANIFEST_FOLDER_ROOT, new ExperimentFileFolder());
    }
}
