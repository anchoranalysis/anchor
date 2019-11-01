package org.anchoranalysis.anchor.overlay.id;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;

public class IDGetterMaskFromOverlay extends IDGetter<ObjMaskWithProperties> {

	private IDGetter<Overlay> delegate;

	private ColoredOverlayCollection oc;

	// If switched on, we always do a modulus of the iteration
	//   with the cfg size to determine the mark to reference
	//
	// This is, for example, useful when ObjMasks are doubled
	//   to include both inside and shell areas, as we can 
	//   still reference the underlying marks
	private boolean modIter;
	
	public IDGetterMaskFromOverlay(IDGetter<Overlay> delegate, ColoredOverlayCollection oc ) {
		this(delegate, oc, false);
	}
	
	public IDGetterMaskFromOverlay(IDGetter<Overlay> delegate, ColoredOverlayCollection oc, boolean modIter ) {
		super();
		this.delegate = delegate;
		this.oc = oc;
		this.modIter = modIter;
	}

	@Override
	public int getID(ObjMaskWithProperties m, int iter) {
		
		if (modIter) {
			iter = iter % oc.size();
		}
		
		// We get a mark from the configuration based upon the iter
		Overlay overlay = oc.get(iter);
		
		return delegate.getID(overlay, iter);
	}
}
