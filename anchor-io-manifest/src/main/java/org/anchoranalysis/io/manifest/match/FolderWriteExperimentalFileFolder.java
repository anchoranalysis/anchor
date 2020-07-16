/* (C)2020 */
package org.anchoranalysis.io.manifest.match;

import org.anchoranalysis.io.manifest.folder.ExperimentFileFolder;
import org.anchoranalysis.io.manifest.folder.FolderWrite;

public class FolderWriteExperimentalFileFolder implements Match<FolderWrite> {

    @Override
    public boolean matches(FolderWrite obj) {
        return obj instanceof ExperimentFileFolder;
    }
}
