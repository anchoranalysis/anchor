package org.anchoranalysis.anchor.overlay;

import org.anchoranalysis.anchor.overlay.id.Identifiable;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;

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
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;

// What can be projected on top of a raster through the GUI
public abstract class Overlay implements Identifiable {
	
	/**
	 * A bounding-box around the overlay
	 * 
	 * @param overlayWriter
	 * @param dim	The dimensions of the containing-scene
	 * 
	 * @return the bounding-box
	 */
	public abstract BoundingBox bbox( DrawOverlay overlayWriter, ImageDimensions dim );
	
	public abstract ObjectWithProperties createScaledMask(
		DrawOverlay overlayWriter,
		double zoomFactorNew,
		ObjectWithProperties om,
		Overlay ol,
		ImageDimensions sdUnscaled,
		ImageDimensions sdScaled,
		BinaryValuesByte bvOut
	) throws CreateException;
	
	public abstract ObjectWithProperties createObject(
		DrawOverlay overlayWriter,
		ImageDimensions dimEntireImage,
		BinaryValuesByte bvOut
	) throws CreateException;
	
	/**
	 * Is a point inside an overlay? (for a particular OverlayWriter).
	 * 
	 * @param overlayWriter
	 * @param pnt
	 * @return
	 */
	public abstract boolean isPointInside( DrawOverlay overlayWriter, Point3i pnt );

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();
	
	public abstract OverlayProperties generateProperties(ImageResolution sr);
}
