/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.provider;

import org.anchoranalysis.anchor.mpp.bean.bound.MarkBounds;
import org.anchoranalysis.core.error.CreateException;

public abstract class MarkBoundsProvider extends MPPProvider<MarkBoundsProvider, MarkBounds> {

    public abstract MarkBounds create() throws CreateException;
}
