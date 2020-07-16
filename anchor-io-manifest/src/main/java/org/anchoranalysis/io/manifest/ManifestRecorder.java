/* (C)2020 */
package org.anchoranalysis.io.manifest;

import java.io.Serializable;
import java.nio.file.Path;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.folder.RootFolder;

public class ManifestRecorder implements Serializable {

    /** */
    private static final long serialVersionUID = -7253272905284863941L;

    private RootFolder rootFolder; // Paths relative to this

    public void init(Path rootFolderPath) {
        rootFolder = new RootFolder(rootFolderPath);
    }

    public FolderWrite getRootFolder() {
        return rootFolder;
    }
}
