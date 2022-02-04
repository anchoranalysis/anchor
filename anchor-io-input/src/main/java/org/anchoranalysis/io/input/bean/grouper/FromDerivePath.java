package org.anchoranalysis.io.input.bean.grouper;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.index.range.IndexRangeNegative;
import org.anchoranalysis.core.system.path.FilePathToUnixStyleConverter;
import org.anchoranalysis.io.input.bean.path.DerivePath;
import org.anchoranalysis.io.input.grouper.InputGrouper;
import org.anchoranalysis.io.input.path.DerivePathException;

/**
 * Derives the grouping-key via a call to {@link DerivePath}.
 *
 * @author Owen Feehan
 */
public abstract class FromDerivePath extends Grouper {

    /**
     * Creates an {@link InputGrouper} that can be used to derive a group-key from a particular
     * input.
     *
     * @param groupIndexRange an index-range to use for grouping, by subsetting components from each
     *     input's identifier.
     * @return the {@link InputGrouper}, if grouping is enabled. Otherwise {@link Optional#empty()}.
     */
    @Override
    public Optional<InputGrouper> createInputGrouper(Optional<IndexRangeNegative> groupIndexRange) {
        return Optional.of(identifier -> deriveGroupKey(identifier));
    }

    /**
     * Selects the {@link DerivePath} to use for deriving a key.
     *
     * @return the selected {@link DerivePath}.
     */
    protected abstract DerivePath selectDerivePath();

    /**
     * Derives a key for the group from {@code identifier}.
     *
     * <p>This key determines which group {@code input} belongs to e.g. like a GROUP BY key in
     * databases.
     *
     * @param identifier an identifier for an input, expressed as a {@link Path}.
     * @return the group key, which will always use forward-slashes as a <i>separator</i>, and never
     *     back-slashes, irrespective of operating-system.
     * @throws DerivePathException if a key cannot be derived from {@code identifier} successfully.
     */
    private String deriveGroupKey(Path identifier) throws DerivePathException {
        Path path = selectDerivePath().deriveFrom(identifier, false);
        assert (!path.isAbsolute());
        return FilePathToUnixStyleConverter.toStringUnixStyle(path);
    }
}
