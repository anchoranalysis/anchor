package overlay;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.OverlayProperties;
import org.anchoranalysis.anchor.overlay.objmask.scaled.FromMask;
import org.anchoranalysis.anchor.overlay.objmask.scaled.ScaledMaskCreator;
import org.anchoranalysis.anchor.overlay.writer.OverlayWriter;

/*-
 * #%L
 * anchor-mpp
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
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;

import ch.ethz.biol.cell.gui.overlay.scaledmask.FromMark;
import ch.ethz.biol.cell.gui.overlay.scaledmask.VolumeThreshold;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMembershipWithFlags;

public class OverlayMark extends Overlay {

	private ScaledMaskCreator scaledMaskCreator;
	private Mark mark;
	private RegionMembershipWithFlags regionMembership;
	
	public OverlayMark(Mark mark, RegionMembershipWithFlags regionMembership) {
		super();
		this.mark = mark;
		this.regionMembership = regionMembership;
		
		/**
		 *	How we create our scaled masks 
		 */
		scaledMaskCreator = new VolumeThreshold(
			new FromMask(),	// Above the threshold, we use the quick *rough* method for scaling up
			new FromMark(regionMembership),	// Below the threshold, we use the slower *fine* method for scaling up 
			5000			// The threshold that decides which to use
		);
	}

	public Mark getMark() {
		return mark;
	}

	@Override
	public BoundingBox bbox(OverlayWriter overlayWriter, ImageDim dim) {
		return mark.bbox(
			dim,
			regionMembership.getRegionID()
		);
	}

	@Override
	public ObjMaskWithProperties createScaledMask(
			OverlayWriter overlayWriter, double zoomFactorNew,
			ObjMaskWithProperties om, Overlay ol, ImageDim sdUnscaled,
			ImageDim sdScaled, BinaryValuesByte bvOut) throws CreateException {
		
		ObjMaskWithProperties omScaled = scaledMaskCreator.createScaledMask(
			overlayWriter,
			om,
			zoomFactorNew,
			mark,
			sdUnscaled,
			bvOut
		);
		//assert( omScaled.getMask().numPixels()>0 );
		//System.out.printf("Scaling %s with %f to %s\n", om, zoomFactorNew, omScaled);
		return omScaled;
	}

	@Override
	public ObjMaskWithProperties createObjMask(OverlayWriter overlayWriter, ImageDim dimEntireImage,
			BinaryValuesByte bvOut) throws CreateException {
		return mark.calcMask(
			dimEntireImage,
			regionMembership,
			bvOut
		);
	}

	@Override
	public int getId() {
		return mark.getId();
	}
	
	@Override
	public boolean isPointInside( OverlayWriter overlayWriter, Point3i pnt ) {
		
		Point3d pntD = PointConverter.doubleFromInt(pnt);
		
		byte membership = mark.evalPntInside(pntD);
		return (regionMembership.isMemberFlag(membership));
	}

	// We delegate uniqueness-check to the mask
	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof OverlayMark) {
			OverlayMark objCast = (OverlayMark) arg0;
			return mark.equals(objCast.mark);	
		} else {
			return false;
		}
		
	}

	@Override
	public int hashCode() {
		return mark.hashCode();
	}

	@Override
	public OverlayProperties generateProperties(ImageRes sr) {
		return mark.generateProperties(sr);
	}
}
