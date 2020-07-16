/* (C)2020 */
package org.anchoranalysis.io.manifest.finder;

import java.util.List;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.match.FolderWriteExperimentalFileFolder;

// Finders
public class FinderExperimentFileFolders implements Finder {

    private List<FolderWrite> list = null;

    @Override
    public boolean doFind(ManifestRecorder manifestRecorder) {
        list =
                FinderUtilities.findListFolder(
                        manifestRecorder, new FolderWriteExperimentalFileFolder());
        return !list.isEmpty();
    }

    @Override
    public boolean exists() {
        return list != null && !list.isEmpty();
    }

    public List<FolderWrite> getList() {
        return list;
    }
}
