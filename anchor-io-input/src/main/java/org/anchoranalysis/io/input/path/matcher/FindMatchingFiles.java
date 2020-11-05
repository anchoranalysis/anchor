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

package org.anchoranalysis.io.input.path.matcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterIncrement;
import org.anchoranalysis.core.progress.TraverseDirectoryForProgressReporter;
import org.anchoranalysis.core.progress.TraverseDirectoryForProgressReporter.TraversalResult;

@AllArgsConstructor
public class FindMatchingFiles {

    /** Recursive whether to recursively iterate through directories */
    private final boolean recursive;

    /** The progress reporter */
    private final ProgressReporter progressReporter;

    public Collection<File> findMatchingFiles(
            Path directory,
            PathMatchConstraints constraints,
            boolean acceptDirectoryErrors,
            Logger logger)
            throws FindFilesException {

        try {
            TraversalResult traversal;
            if (recursive) {
                traversal =
                        TraverseDirectoryForProgressReporter.traverseRecursive(
                                directory,
                                20,
                                constraints.getPredicates().getDirectory(),
                                constraints.getMaxDirectoryDepth());
            } else {
                traversal =
                        TraverseDirectoryForProgressReporter.traverseNotRecursive(
                                directory, constraints.getPredicates().getDirectory());
            }
            return convertToList(traversal, constraints, acceptDirectoryErrors, logger);

        } catch (IOException e) {
            throw new FindFilesException("A failure occurred searching a directory for files");
        }
    }

    private List<File> convertToList(
            TraversalResult traversal,
            PathMatchConstraints constraints,
            boolean acceptDirectoryErrors,
            Logger logger)
            throws FindFilesException {

        List<File> listOut = new ArrayList<>();

        List<Path> leafDirectories =
                filterLeafDirectories(
                        traversal.getLeafDirectories(), constraints.getPredicates().getDirectory());

        ProgressReporterIncrement pri = new ProgressReporterIncrement(progressReporter);
        pri.open();
        pri.setMin(0);
        pri.setMax(leafDirectories.size() + 1);

        // We first check the files that we remembered from our folder search
        filesFromDirectorySearch(
                traversal.getFiles(), constraints.getPredicates().getFile(), listOut);

        pri.update();

        int remainingDirDepth = constraints.getMaxDirectoryDepth() - traversal.getDepth() + 1;
        assert remainingDirDepth >= 1;
        otherFiles(
                pri,
                leafDirectories,
                constraints.replaceMaxDirDepth(remainingDirDepth),
                listOut,
                acceptDirectoryErrors,
                logger);

        pri.close();

        return listOut;
    }

    private static List<Path> filterLeafDirectories(
            List<Path> leafDirectories, Predicate<Path> dirMatcher) {
        return FunctionalList.filterToList(leafDirectories, dirMatcher);
    }

    private static void filesFromDirectorySearch(
            List<Path> filesOut, Predicate<Path> matcher, List<File> listOut) {
        for (Path p : filesOut) {
            if (matcher.test(p)) {
                listOut.add(p.normalize().toFile());
            }
        }
    }

    private void otherFiles(
            ProgressReporterIncrement pri,
            List<Path> progressDirectories,
            PathMatchConstraints pathMatchConstraints,
            List<File> listOut,
            boolean acceptDirectoryErrors,
            Logger logger)
            throws FindFilesException {
        // Then every other folder is treated as a bucket
        for (Path dirProgress : progressDirectories) {

            try {
                WalkSingleDirectory.apply(dirProgress, pathMatchConstraints, listOut);
            } catch (FindFilesException e) {
                if (acceptDirectoryErrors) {
                    logger.errorReporter().recordError(FindMatchingFiles.class, e);
                } else {
                    // Rethrow the exception
                    throw e;
                }
            } finally {
                pri.update();
            }
        }
    }
}
