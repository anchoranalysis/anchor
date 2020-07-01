package org.anchoranalysis.anchor.overlay.id;

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
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;

public class IDGetterMaskFromOverlay extends IDGetter<ObjectWithProperties> {

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
	public int getID(ObjectWithProperties m, int iter) {
		
		if (modIter) {
			iter = iter % oc.size();
		}
		
		// We get a mark from the configuration based upon the iter
		Overlay overlay = oc.get(iter);
		
		return delegate.getID(overlay, iter);
	}
}
