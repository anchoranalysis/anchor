package org.anchoranalysis.io.input.bean.grouper;

import java.nio.file.Path;
import org.anchoranalysis.core.system.path.FilePathToUnixStyleConverter;
import org.anchoranalysis.io.input.bean.path.DerivePath;
import org.anchoranalysis.io.input.path.DerivePathException;

/**
 * Derives the grouping-key via a call to {@link DerivePath}.
 *
 * @author Owen Feehan
 */
public abstract class FromDerivePath extends Grouper {

    @Override
    public boolean isGroupingEnabled() {
        return true;
    }

    @Override
    public String deriveGroupKey(Path identifier) throws DerivePathException {
        assert (isGroupingEnabled());
        Path path = selectDerivePath().deriveFrom(identifier, false);
        assert (!path.isAbsolute());
        return FilePathToUnixStyleConverter.toStringUnixStyle(path);
    }

    /**
     * Selects the {@link DerivePath} to use for deriving a key.
     *
     * @return the selected {@link DerivePath}.
     */
    protected abstract DerivePath selectDerivePath();
}
