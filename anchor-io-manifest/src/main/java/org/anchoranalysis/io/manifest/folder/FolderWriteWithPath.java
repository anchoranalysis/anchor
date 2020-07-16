/* (C)2020 */
package org.anchoranalysis.io.manifest.folder;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.anchoranalysis.io.filepath.FilePathToUnixStyleConverter;

public abstract class FolderWriteWithPath extends FolderWrite {

    /** */
    private static final long serialVersionUID = 8319222670306262133L;

    // Relative path to parent. As this becomes serialized, we store is a string
    private String path;

    @Override
    public Path getRelativePath() {
        assert (path != null);
        return Paths.get(path);
    }

    public void setPath(Path path) {
        this.path = FilePathToUnixStyleConverter.toStringUnixStyle(path);
    }
}
