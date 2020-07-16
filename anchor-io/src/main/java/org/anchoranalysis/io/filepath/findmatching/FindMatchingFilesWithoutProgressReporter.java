/* (C)2020 */
package org.anchoranalysis.io.filepath.findmatching;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.anchoranalysis.core.log.Logger;

public class FindMatchingFilesWithoutProgressReporter implements FindMatchingFiles {

    @Override
    public Collection<File> apply(
            Path dir,
            PathMatchConstraints constraints,
            boolean acceptDirectoryErrors,
            Logger logger)
            throws FindFilesException {

        List<File> listOut = new ArrayList<>();
        try {
            WalkSingleDir.apply(dir, constraints, listOut);
        } catch (FindFilesException e) {
            if (acceptDirectoryErrors) {
                logger.errorReporter().recordError(FindMatchingFilesWithProgressReporter.class, e);
            } else {
                // Rethrow the exception
                throw e;
            }
        }
        return listOut;
    }
}
