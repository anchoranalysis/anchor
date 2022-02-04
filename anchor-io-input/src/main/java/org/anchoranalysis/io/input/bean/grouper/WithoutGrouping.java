package org.anchoranalysis.io.input.bean.grouper;

import java.util.Optional;
import org.anchoranalysis.core.index.range.IndexRangeNegative;
import org.anchoranalysis.io.input.grouper.InputGrouper;

/**
 * Avoids grouping of inputs, effectively processing all inputs as one large group.
 *
 * @author Owen Feehan
 */
public class WithoutGrouping extends Grouper {

    @Override
    public Optional<InputGrouper> createInputGrouper(Optional<IndexRangeNegative> groupIndexRange) {
        return Optional.empty();
    }
}
