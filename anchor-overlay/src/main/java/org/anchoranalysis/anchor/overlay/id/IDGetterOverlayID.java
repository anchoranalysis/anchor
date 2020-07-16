/* (C)2020 */
package org.anchoranalysis.anchor.overlay.id;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.core.idgetter.IDGetter;

public class IDGetterOverlayID implements IDGetter<Overlay> {

    @Override
    public int getID(Overlay ol, int iter) {
        return ol.getId();
    }
}
