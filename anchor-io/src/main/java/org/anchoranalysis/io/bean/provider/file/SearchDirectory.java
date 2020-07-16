/* (C)2020 */
package org.anchoranalysis.io.bean.provider.file;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.bean.file.matcher.FileMatcher;
import org.anchoranalysis.io.bean.file.matcher.MatchGlob;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.error.FileProviderException;
import org.anchoranalysis.io.glob.GlobExtractor;
import org.anchoranalysis.io.glob.GlobExtractor.GlobWithDirectory;

public class SearchDirectory extends FileProviderWithDirectoryString {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private FileMatcher matcher;

    @BeanField @Getter @Setter private boolean recursive = false;

    /** If non-negative the max depth of directories. If -1, then there is no maximum depth. */
    @BeanField @Getter @Setter private int maxDirectoryDepth = -1;

    /**
     * if TRUE, case is ignored in the pattern matching. Otherwise the system-default is used i.e.
     * Windows ignores case, Linux doesn't
     */
    @BeanField @Getter @Setter private boolean ignoreHidden = true;

    /**
     * if TRUE, continues when a directory-access-error occurs (logging it), otherwise throws an
     * exception
     */
    @BeanField @Getter @Setter private boolean acceptDirectoryErrors = false;
    // END BEAN PROPERTIES

    // Matching files
    @Override
    public Collection<File> matchingFilesForDirectory(Path directory, InputManagerParams params)
            throws FileProviderException {

        int maxDirDepth =
                maxDirectoryDepth >= 0
                        ? maxDirectoryDepth
                        : Integer.MAX_VALUE; // maxDepth of directories searches
        try {
            return matcher.matchingFiles(
                    directory, recursive, ignoreHidden, acceptDirectoryErrors, maxDirDepth, params);
        } catch (AnchorIOException e) {
            throw new FileProviderException(e);
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

        GlobWithDirectory gwd = GlobExtractor.extract(combinedFileFilter.toString());

        MatchGlob matcherGlob = new MatchGlob();
        matcherGlob.setGlob(gwd.getGlob());

        setDirectory(gwd.getDirectory().orElse(""));

        this.matcher = matcherGlob;
    }
}
