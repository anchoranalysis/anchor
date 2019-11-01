package org.anchoranalysis.anchor.overlay.collection;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.anchor.overlay.objmask.OverlayObjMask;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;

/**
 * Two-way factory.
 * 
 * Creation of OverlayCollection from marks
 * Retrieval of marks back from OverlayCollections
 * 
 * @author Owen Feehan
 *
 */
public class OverlayCollectionObjMaskFactory {
	
	public static OverlayCollection createWithoutColor( ObjMaskCollection objs, IDGetter<ObjMask> idGetter ) {
		OverlayCollection out = new OverlayCollection();
		
		for(int i=0; i<objs.size(); i++) {
			ObjMask om = objs.get(i);
			
			int id = idGetter.getID(om, i);
			
			out.add( new OverlayObjMask(om, id) );
		}
		
		return out;
	}
	
	// Creates objs from whatever Overlays are found in the collection
	public static ObjMaskCollection objsFromOverlays( OverlayCollection overlays ) {
		ObjMaskCollection out = new ObjMaskCollection();
		
		for(int i=0; i<overlays.size(); i++) {
			Overlay overlay = overlays.get(i);
			
			if (overlay instanceof OverlayObjMask) {
				OverlayObjMask overlayCast = (OverlayObjMask) overlay;
				out.add( overlayCast.getObjMask().getMask() );
			}
		}
		return out;
	}
}
