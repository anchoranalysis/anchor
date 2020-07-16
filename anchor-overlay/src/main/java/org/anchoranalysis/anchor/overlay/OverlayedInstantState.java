/* (C)2020 */
package org.anchoranalysis.anchor.overlay;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.index.SingleIndexCntr;

@EqualsAndHashCode(callSuper = true)
public class OverlayedInstantState extends SingleIndexCntr {

    @Getter private final OverlayCollection overlayCollection;

    public OverlayedInstantState(int iter, OverlayCollection overlayCollection) {
        super(iter);
        this.overlayCollection = overlayCollection;
    }
}
