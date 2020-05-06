package org.anchoranalysis.anchor.overlay.collection;

/*-
 * #%L
 * anchor-overlay
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import org.anchoranalysis.anchor.overlay.Overlay;
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
