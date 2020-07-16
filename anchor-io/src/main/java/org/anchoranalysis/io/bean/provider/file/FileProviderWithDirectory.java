/* (C)2020 */
package org.anchoranalysis.io.bean.provider.file;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.FileProviderException;
import org.anchoranalysis.io.params.InputContextParams;

public abstract class FileProviderWithDirectory extends FileProvider {

    public abstract Path getDirectoryAsPath(InputContextParams inputContext);

    @Override
    public final Collection<File> create(InputManagerParams params) throws FileProviderException {
        return matchingFilesForDirectory(getDirectoryAsPath(params.getInputContext()), params);
    }

    public abstract Collection<File> matchingFilesForDirectory(
            Path directory, InputManagerParams params) throws FileProviderException;

    /** Like getDirectory as Path but converts any relative path to absolute one */
    public Path getDirectoryAsPathEnsureAbsolute(InputContextParams inputContext) {
        return makeAbsolutePathIfNecessary(getDirectoryAsPath(inputContext));
    }

    /**
     * If path is absolute, it's returned as-is If path is relative, and the 'makeAbsolute' option
     * is activated, it's added to the localizedPath
     *
     * @param path
     * @return
     */
    private Path makeAbsolutePathIfNecessary(Path path) {
        if (path.isAbsolute()) {
            return path;
        } else {
            Path parent = getLocalPath().getParent();
            return parent.resolve(path);
        }
    }
}
