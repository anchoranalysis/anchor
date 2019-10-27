package ch.ethz.biol.cell.mpp.cfgtoobjmaskwriter;

/*
 * #%L
 * anchor-overlay
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objmask.properties.IDGetterObjMaskWithProperties;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

import ch.ethz.biol.cell.gui.overlay.Overlay;
import ch.ethz.biol.cell.gui.overlay.ColoredOverlayCollection;
import ch.ethz.biol.cell.imageprocessing.io.objmask.ObjMaskWriter;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMembershipWithFlags;

/**
 * Writes overlays onto a RGBStack. Also writes precalculated overlays.
 * 
 * @author Owen Feehan
 *
 */
public abstract class OverlayWriter {
	
	public abstract ObjMaskWriter getObjMaskWriter();
	
	public abstract RegionMembershipWithFlags getRegionMembership();
	
	/**
	 * Writes a collection of colored-overlays to the background
	 * 
	 * @param oc			overlays
	 * @param stack			overlays are written onto this stack
	 * @param idGetter		
	 * @param factory
	 * @throws OperationFailedException
	 */
	public void writeOverlays( ColoredOverlayCollection oc, RGBStack stack, IDGetter<Overlay> idGetter ) throws OperationFailedException {
		writeOverlays(oc, stack, idGetter, 1.0);
	}
	
	private void writeOverlays( ColoredOverlayCollection oc, RGBStack stack, IDGetter<Overlay> idGetter, double zMultFactor ) throws OperationFailedException {
		writeOverlays(
			oc,
			stack.getDimensions(),
			stack,
			idGetter,
			new BoundingBox(stack.getDimensions().getExtnt()),
			1.0
		);
	}

	//
	// It's a two step process
	//   First we generate ObjMasks for the configuration
	//   Then these are written to the RGBMask
	//
	//   We split the steps in two, so that they can be potentially cached
	//
	private void writeOverlays( ColoredOverlayCollection oc, ImageDim dim, RGBStack background, IDGetter<Overlay> idGetter, BoundingBox bboxContainer, double zoomFactor ) throws OperationFailedException {
		
		try {
			List<PrecalcOverlay> masksPreprocessed = precalculate( oc, this, dim, BinaryValues.getDefault().createByte() );
			
			// TODO, can't we read the color directly from the cfg in some way?
			writePrecalculatedOverlays(masksPreprocessed, oc, dim, background, idGetter, new IDGetterObjMaskWithProperties("colorID"), bboxContainer );
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
	}
		
	// dim should be for the ENTIRE cfg, not just the bit in bboxContainer
	public abstract void writePrecalculatedOverlays( List<PrecalcOverlay> precalculatedMasks, ColoredOverlayCollection overlays, ImageDim dim, RGBStack background, IDGetter<Overlay> idGetter, IDGetter<ObjMaskWithProperties> idGetterColor, BoundingBox bboxContainer ) throws OperationFailedException;
	
	public abstract void writeOverlaysIfIntersects( ColoredOverlayCollection oc, RGBStack stack, IDGetter<Overlay> idGetter, List<BoundingBox> intersectList ) throws OperationFailedException;
	

	
	
	// Does computationally-intensive preprocessing (so it can be cached). Any object can be used, but there should be exactly one object
	//  per Mark in the cfg, in the same order as the Cfg is inputted
	public static List<PrecalcOverlay> precalculate(ColoredOverlayCollection coc, OverlayWriter maskWriter, ImageDim dim, BinaryValuesByte bvOut) throws CreateException {
		
		List<PrecalcOverlay> listOut = new ArrayList<PrecalcOverlay>();
		
		IDGetterIter<Overlay> colorIDGetter = new IDGetterIter<Overlay>();
		
		for( int i=0; i< coc.size(); i++ ) {
			
			Overlay ol = coc.get(i);
			ObjMaskWithProperties om = ol.createObjMask(maskWriter, dim, bvOut);
			
			om.setProperty("colorID", colorIDGetter.getID(ol, i));
			
			PrecalcOverlay cfgPrecalc = createPrecalc( maskWriter, om, dim );
			listOut.add( cfgPrecalc );
		}

		return listOut;
	}
	
	
	public static PrecalcOverlay createPrecalc( OverlayWriter maskWriter, ObjMaskWithProperties om, ImageDim dim ) throws CreateException {
		Object writerPrecalc = maskWriter.getObjMaskWriter().precalculate(om, dim);
		return new PrecalcOverlay(om,writerPrecalc);
	}
}
