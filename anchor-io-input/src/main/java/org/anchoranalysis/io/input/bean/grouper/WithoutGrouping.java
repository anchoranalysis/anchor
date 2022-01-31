package org.anchoranalysis.io.input.bean.grouper;

import java.nio.file.Path;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;

/**
 * Avoids grouping of inputs, effectively processing all inputs as one large group.
 *
 * @author Owen Feehan
 */
public class WithoutGrouping extends Grouper {

    @Override
    public boolean isGroupingEnabled() {
        return false;
    }

    @Override
    public String deriveGroupKey(Path identifier) {
        throw new AnchorImpossibleSituationException();
    }
}
