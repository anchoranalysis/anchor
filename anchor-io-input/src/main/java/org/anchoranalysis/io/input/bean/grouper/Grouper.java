package org.anchoranalysis.io.input.bean.grouper;

import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.index.range.IndexRangeNegative;
import org.anchoranalysis.io.input.grouper.InputGrouper;

/**
 * Determines how partition inputs into groups.
 *
 * <p>This is typically based upon the input-identifiers.
 *
 * @author Owen Feehan
 */
public abstract class Grouper extends AnchorBean<Grouper> {

    /**
     * Creates an {@link InputGrouper} that can be used to derive a group-key from a particular
     * input.
     *
     * @param groupIndexRange an index-range to use for grouping, by subsetting components from each
     *     input's identifier.
     * @eturn the {@link InputGrouper}, if grouping is enabled. Otherwise {@link Optional#empty()}.
     */
    public abstract Optional<InputGrouper> createInputGrouper(
            Optional<IndexRangeNegative> groupIndexRange);
}
