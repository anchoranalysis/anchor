/* (C)2020 */
package org.anchoranalysis.io.manifest.match;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.anchoranalysis.io.manifest.folder.FolderWrite;

public class FolderWritePath implements Match<FolderWrite> {

    private Path path;

    public FolderWritePath(String path) {
        this(Paths.get(path));
    }

    public FolderWritePath(Path path) {
        super();
        this.path = path;
    }

    @Override
    public boolean matches(FolderWrite obj) {
        return obj.getRelativePath().equals(path);
    }
}
