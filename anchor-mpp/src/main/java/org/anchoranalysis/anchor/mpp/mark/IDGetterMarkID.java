/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark;

import org.anchoranalysis.core.idgetter.IDGetter;

public class IDGetterMarkID implements IDGetter<Mark> {

    @Override
    public int getID(Mark m, int iter) {
        return m.getId();
    }
}
