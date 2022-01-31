package org.anchoranalysis.io.input.bean.path;

import java.nio.file.Path;
import org.anchoranalysis.io.input.path.DerivePathException;

/**
 * Returns the same {@link Path} as the argument passed in.
 *
 * <p>i.e. the {@link Path} is unchanged.
 *
 * @author Owen Feehan
 */
public class Identity extends DerivePath {

    @Override
    public Path deriveFrom(Path source, boolean debugMode) throws DerivePathException {
        return source;
    }
}
