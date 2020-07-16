/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.provider;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.error.CreateException;

public abstract class MarkProvider extends MPPProvider<MarkProvider, Optional<Mark>> {

    public abstract Optional<Mark> create() throws CreateException;
}
