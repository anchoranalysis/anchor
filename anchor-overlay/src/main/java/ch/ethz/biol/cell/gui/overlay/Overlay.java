package ch.ethz.biol.cell.gui.overlay;

import org.anchoranalysis.anchor.mpp.mark.OverlayProperties;

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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;

import ch.ethz.biol.cell.core.IHasIdentifier;
import ch.ethz.biol.cell.mpp.cfgtoobjmaskwriter.OverlayWriter;

// What can be projected on top of a raster through the GUI
public abstract class Overlay implements IHasIdentifier {
	
	/**
	 * A bounding-box around the overlay
	 * @param overlayWriter TODO
	 * @param dim	The dimensions of the containing-scene
	 * 
	 * @return the bounding-box
	 */
	protected abstract BoundingBox bbox( OverlayWriter overlayWriter, ImageDim dim );
	
	public abstract ObjMaskWithProperties createScaledMask(
		OverlayWriter overlayWriter,
		double zoomFactorNew,
		ObjMaskWithProperties om,
		Overlay ol,
		ImageDim sdUnscaled,
		ImageDim sdScaled,
		BinaryValuesByte bvOut
	) throws CreateException;
	
	public abstract ObjMaskWithProperties createObjMask(
		OverlayWriter overlayWriter,
		ImageDim dimEntireImage,
		BinaryValuesByte bvOut
	) throws CreateException;
	
	/**
	 * Is a point inside an overlay? (for a particular OverlayWriter).
	 * 
	 * @param overlayWriter
	 * @param pnt
	 * @return
	 */
	public abstract boolean isPointInside( OverlayWriter overlayWriter, Point3i pnt );

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();
	
	public abstract OverlayProperties generateProperties(ImageRes sr);
}
