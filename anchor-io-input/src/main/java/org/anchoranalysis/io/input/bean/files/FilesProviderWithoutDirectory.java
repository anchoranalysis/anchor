package org.anchoranalysis.io.input.bean.files;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.io.input.InputContextParams;

/**
 * Base class for implementations of {@link FilesProvider} which <b>do not</b> have an associated
 * directory.
 *
 * @author Owen Feehan
 */
public abstract class FilesProviderWithoutDirectory extends FilesProvider {

    @Override
    public Optional<Path> rootDirectory(InputContextParams inputContext) {
        return Optional.empty();
    }
}
