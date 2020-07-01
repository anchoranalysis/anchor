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

import org.anchoranalysis.anchor.overlay.objmask.OverlayObjMask;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Two-way factory.
 * 
 * <p>Creation of {@link OverlayCollection} from marks
 * Retrieval of marks back from {@link OverlayCollection}s</p>
 * 
 * @author Owen Feehan
 *
 */
public class OverlayCollectionObjectFactory {
	
	public static OverlayCollection createWithoutColor( ObjectCollection objs, IDGetter<ObjectMask> idGetter ) {
		OverlayCollection out = new OverlayCollection();
		
		for(int i=0; i<objs.size(); i++) {
			ObjectMask om = objs.get(i);
			
			int id = idGetter.getID(om, i);
			
			out.add(
				new OverlayObjMask(om, id)
			);
		}
		
		return out;
	}
	
	/** Creates objects from whatever Overlays are found in the collection **/
	public static ObjectCollection objsFromOverlays( OverlayCollection overlays ) {

		// Extract mask from any overlays that are OverlayObjMask
		return ObjectCollectionFactory.filterAndMapFrom(
			overlays.asList(),
			overlay->overlay instanceof OverlayObjMask,
			overlay-> ((OverlayObjMask) overlay).getObjMask().getMask()
		);
	}
}
