package org.anchoranalysis.io.input.bean.grouper;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.functional.OptionalFactory;
import org.anchoranalysis.io.input.path.DerivePathException;

/**
 * Determines how partition inputs into groups.
 *
 * <p>This is typically based upon the input-identifiers.
 *
 * @author Owen Feehan
 */
public abstract class Grouper extends AnchorBean<Grouper> {

    /**
     * Whether the grouping is enabled.
     *
     * @return true if inputs may be divided into groups, false if are guaranteed to always belong
     *     to a single group.
     */
    public abstract boolean isGroupingEnabled();

    /**
     * Like {@link #deriveGroupKey(Path)} but can also be called when grouping is disabled.
     *
     * @param identifier an identifier for an input, expressed as a {@link Path}.
     * @return the result of {@link #deriveGroupKey(Path)} when {@code isGroupingEnabled()==true), otherwise {@link Optional#empty()}.
     * @throws DerivePathException if a key cannot be derived from {@code identifier} successfully.
     */
    public Optional<String> deriveGroupKeyOptional(Path identifier) throws DerivePathException {
        return OptionalFactory.createChecked(isGroupingEnabled(), () -> deriveGroupKey(identifier));
    }

    /**
     * Derives a key for the group from {@code identifier}.
     *
     * <p>This key determines which group {@code input} belongs to e.g. like a GROUP BY key in
     * databases.
     *
     * <p>This method should <b>only</b> be called, after checking that {@link #isGroupingEnabled()}
     * is true.
     *
     * @param identifier an identifier for an input, expressed as a {@link Path}.
     * @return the group key, which will always use forward-slashes as a <i>separator</i>, and never
     *     back-slashes, irrespective of operating-system.
     * @throws DerivePathException if a key cannot be derived from {@code identifier} successfully.
     */
    public abstract String deriveGroupKey(Path identifier) throws DerivePathException;
}
