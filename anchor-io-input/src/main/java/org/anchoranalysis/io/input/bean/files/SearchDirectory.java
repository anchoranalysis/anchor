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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.bean.InputManagerParams;
import org.anchoranalysis.io.input.bean.path.matcher.MatchGlob;
import org.anchoranalysis.io.input.bean.path.matcher.PathMatcher;
import org.anchoranalysis.io.input.file.FilesProviderException;
import org.anchoranalysis.io.input.path.GlobExtractor;
import org.anchoranalysis.io.input.path.GlobExtractor.GlobWithDirectory;

/**
 * Searches a directory for files whose paths match a particular predicate.
 *
 * <p>By default, the search does <b>not</b> occur recursively, but the bean-property {@code
 * recursive} enables this.
 *
 * @author Owen Feehan
 */
public class SearchDirectory extends FilesProviderWithDirectoryString {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private PathMatcher matcher;

    /** If true the search is applied recursively over sub-directories. */
    @BeanField @Getter @Setter private boolean recursive = false;

    /** If non-negative the max depth of directories. If -1, then there is no maximum depth. */
    @BeanField @Getter @Setter private int maxDirectoryDepth = -1;

    /**
     * if true, case is ignored in the pattern matching. Otherwise the system-default is used i.e.
     * Windows ignores case, Linux doesn't
     */
    @BeanField @Getter @Setter private boolean ignoreHidden = true;

    /**
     * if true, continues when a directory-access-error occurs (logging it), otherwise throws an
     * exception
     */
    @BeanField @Getter @Setter private boolean acceptDirectoryErrors = false;

    /** If true, the files are sorted after being searched, to achieve a deterministic order. */
    private boolean sort = true;
    // END BEAN PROPERTIES

    // Matching files
    @Override
    public Collection<File> matchingFilesForDirectory(Path directory, InputManagerParams params)
            throws FilesProviderException {

        Optional<Integer> maxDirectoryDepthOptional =
                OptionalUtilities.createFromFlag(maxDirectoryDepth >= 0, maxDirectoryDepth);
        try {
            List<File> filesUnsorted =
                    matcher.matchingFiles(
                            directory,
                            recursive,
                            ignoreHidden,
                            acceptDirectoryErrors,
                            maxDirectoryDepthOptional,
                            Optional.of(params));
            if (sort) {
                Collections.sort(filesUnsorted);
            }
            return filesUnsorted;
        } catch (InputReadFailedException e) {
            throw new FilesProviderException(e);
        }
    }

    /**
     * Sets both the fileFilter and the Directory from a combinedFileFilter string
     *
     * <p>This is a glob matching e.g. somefilepath/*.tif or somefilepath\*.tif
     *
     * @param combinedFileFilter
     */
    public void setFileFilterAndDirectory(Path combinedFileFilter) {

        GlobWithDirectory glob = GlobExtractor.extract(combinedFileFilter.toString());

        MatchGlob matcherGlob = new MatchGlob();
        matcherGlob.setGlob(glob.getGlob());

        setDirectory(glob.getDirectory().orElse(""));

        this.matcher = matcherGlob;
    }
}
