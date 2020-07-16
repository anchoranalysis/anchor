/* (C)2020 */
package org.anchoranalysis.io.output.bound;

import java.nio.file.Path;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;

/**
 * Like an existing bound-context but redirects all output into a sub-folder
 *
 * @author Owen Feehan
 */
class RedirectIntoSubdirectory implements BoundIOContext {

    private BoundIOContext delegate;
    private BoundOutputManagerRouteErrors replacementOutputManager;

    public RedirectIntoSubdirectory(
            BoundIOContext delegate,
            String folderPath,
            ManifestFolderDescription manifestDescription) {
        super();
        this.delegate = delegate;
        this.replacementOutputManager =
                delegate.getOutputManager().deriveSubdirectory(folderPath, manifestDescription);
    }

    @Override
    public BoundOutputManagerRouteErrors getOutputManager() {
        return replacementOutputManager;
    }

    @Override
    public Path getModelDirectory() {
        return delegate.getModelDirectory();
    }

    @Override
    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    @Override
    public Logger getLogger() {
        return delegate.getLogger();
    }
}
