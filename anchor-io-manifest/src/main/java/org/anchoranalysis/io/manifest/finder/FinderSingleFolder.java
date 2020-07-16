/* (C)2020 */
package org.anchoranalysis.io.manifest.finder;

import java.util.Optional;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.folder.FolderWrite;

public abstract class FinderSingleFolder implements Finder {

    private Optional<FolderWrite> foundFolder = Optional.empty();

    // A simple method to override in each finder that is based upon finding a single file
    protected abstract Optional<FolderWrite> findFolder(ManifestRecorder manifestRecorder);

    @Override
    public final boolean doFind(ManifestRecorder manifestRecorder) {

        foundFolder = findFolder(manifestRecorder);

        return exists();
    }

    @Override
    public final boolean exists() {
        return foundFolder.isPresent();
    }

    protected FolderWrite getFoundFolder() {
        return foundFolder.get();
    }
}
