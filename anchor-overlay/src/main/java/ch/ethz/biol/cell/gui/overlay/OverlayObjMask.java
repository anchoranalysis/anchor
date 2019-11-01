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
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;

import ch.ethz.biol.cell.gui.overlay.Overlay;
import ch.ethz.biol.cell.gui.overlay.scaledmask.FromMask;
import ch.ethz.biol.cell.gui.overlay.scaledmask.ScaledMaskCreator;
import ch.ethz.biol.cell.mpp.cfgtoobjmaskwriter.OverlayWriter;

public class OverlayObjMask extends Overlay {

	private static ScaledMaskCreator scaledMaskCreator = new FromMask();
	
	private ObjMaskWithProperties om;
	private int id;		// We cache the ID to make quick to laod
	
	public OverlayObjMask( ObjMask om, int id ) {
		super();
		this.om = new ObjMaskWithProperties( om );
		this.om.getProperties().put("id", id);
		this.id = id;
	}

	// Assumes object mask is always inside the dim. TODO verify that is valid.
	@Override
	protected BoundingBox bbox(OverlayWriter overlayWriter, ImageDim dim) {
		assert( dim.contains( om.getBoundingBox()) );
		return om.getBoundingBox();
	}

	@Override
	public ObjMaskWithProperties createScaledMask(
			OverlayWriter overlayWriter, double zoomFactorNew,
			ObjMaskWithProperties om, Overlay ol, ImageDim sdUnscaled,
			ImageDim sdScaled, BinaryValuesByte bvOut) throws CreateException {
		
		return scaledMaskCreator.createScaledMask(
			overlayWriter,
			om,
			zoomFactorNew,
			om,
			sdScaled,
			bvOut
		);
	}

	// TODO do we need to duplicate here?
	@Override
	public ObjMaskWithProperties createObjMask(OverlayWriter overlayWriter, ImageDim dimEntireImage,
			BinaryValuesByte bvOut) throws CreateException {
		return om;
	}

	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public boolean isPointInside( OverlayWriter overlayWriter, Point3i pnt ) {
		return om.getMask().contains(pnt);
	}

	// We delegate uniqueness-check to the mask
	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof OverlayObjMask) {
			OverlayObjMask objCast = (OverlayObjMask) arg0;
			return this.om.getMask().equals(objCast.om.getMask());	
		} else {
			return false;
		}
		
	}

	@Override
	public int hashCode() {
		return om.getMask().hashCode();
	}

	@Override
	public OverlayProperties generateProperties(ImageRes sr) {
		// TODO take the properties from the object mask
		OverlayProperties out = new OverlayProperties();
		out.add("id", id);
		return out;
	}

	public ObjMaskWithProperties getObjMask() {
		return om;
	}
}
