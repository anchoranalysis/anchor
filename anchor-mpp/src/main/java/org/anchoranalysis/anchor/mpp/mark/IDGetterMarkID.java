package org.anchoranalysis.anchor.mpp.mark;

import org.anchoranalysis.core.idgetter.IDGetter;

public class IDGetterMarkID extends IDGetter<Mark> {

	@Override
	public int getID( Mark m, int iter ) {
		return m.getId();
	}
}
