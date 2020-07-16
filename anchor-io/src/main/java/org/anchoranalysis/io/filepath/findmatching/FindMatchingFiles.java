/* (C)2020 */
package org.anchoranalysis.io.filepath.findmatching;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import org.anchoranalysis.core.log.Logger;

public interface FindMatchingFiles {

    Collection<File> apply(
            Path dir,
            PathMatchConstraints constraints,
            boolean acceptDirectoryErrors,
            Logger logger)
            throws FindFilesException;
}
