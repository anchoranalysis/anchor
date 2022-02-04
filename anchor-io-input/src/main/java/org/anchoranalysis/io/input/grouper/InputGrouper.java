package org.anchoranalysis.io.input.grouper;

import java.nio.file.Path;
import org.anchoranalysis.io.input.path.DerivePathException;

/**
 * Derives a grouping key from an input.
 *
 * @author Owen Feehan
 */
@FunctionalInterface
public interface InputGrouper {

    /**
     * Derives a key for the group from {@code identifier}.
     *
     * <p>This key determines which group {@code input} belongs to e.g. like a GROUP BY key in
     * databases.
     *
     * @param identifier an identifier for an input, expressed as a {@link Path}.
     * @return a derived grouping-key.
     * @throws DerivePathException if a key cannot be derived from {@code identifier} successfully.
     */
    String deriveGroupKeyOptional(Path identifier) throws DerivePathException;
}
