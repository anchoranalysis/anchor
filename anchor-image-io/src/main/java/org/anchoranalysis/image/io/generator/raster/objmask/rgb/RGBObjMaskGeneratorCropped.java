package org.anchoranalysis.image.io.generator.raster.objmask.rgb;

import org.anchoranalysis.anchor.overlay.bean.objmask.writer.ObjMaskWriter;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.CreateException;

/*
 * #%L
 * anchor-image-io
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


import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.io.stack.ConvertDisplayStackToRGB;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.objmask.ops.ObjMaskMerger;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithPropertiesCollection;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.rgb.RGBStack;

/**
 * Similar to {@link RGBObjMaskGenerator}
 * 
 * BUT with the background stack cropped to contain the objects a small margin
 * 
 * @author Owen Feehan
 *
 */
public class RGBObjMaskGeneratorCropped extends RGBObjMaskGeneratorBaseWithBackground {
	
	private int marginXY = 0;
	private int marginZ = 0;
	
	private BoundingBox bbox;

	public RGBObjMaskGeneratorCropped(ObjMaskWriter objMaskWriter,	DisplayStack background, ColorIndex colorIndex) {
		this(objMaskWriter, background, colorIndex, new IDGetterIter<ObjMaskWithProperties>(), new IDGetterIter<ObjMaskWithProperties>() );
	}

	private RGBObjMaskGeneratorCropped(ObjMaskWriter objMaskWriter, DisplayStack background, ColorIndex colorIndex, IDGetter<ObjMaskWithProperties> idGetter, IDGetter<ObjMaskWithProperties> colorIDGetter) {
		super(objMaskWriter, colorIndex, idGetter, colorIDGetter, background);
	}
	
	public int getMarginXY() {
		return marginXY;
	}

	public void setMarginXY(int marginXY) {
		this.marginXY = marginXY;
	}

	public int getMarginZ() {
		return marginZ;
	}

	public void setMarginZ(int marginZ) {
		this.marginZ = marginZ;
	}

	@Override
	protected RGBStack generateBackground() throws CreateException {
		try {
			ObjMaskCollection objs = getIterableElement().collectionObjMask();
			
			if (objs.isEmpty()) {
				throw new CreateException("This generator expects at least one mask to be present");
			}
			
			// Get a bounding box that contains all the objects
			this.bbox = ObjMaskMerger.mergeBBoxFromObjs(objs);
			
			growBBBox(bbox, getBackground().getDimensions().getExtnt() );
			
			// Extract the relevant piece of background
			return ConvertDisplayStackToRGB.convertCropped(
				getBackground(),
				bbox
			);
		} catch (OperationFailedException e) {
			throw new CreateException(e);
		}
	}

	@Override
	protected ObjMaskWithPropertiesCollection generateMasks() throws CreateException {
		// Create a new set of object masks, relative to the bbox position
		return relTo( getIterableElement().collectionObjMask(), bbox );
	}

	private void growBBBox(BoundingBox bbox, Extent containingScene ) {
		assert(marginXY>=0);
		assert(marginZ>=0);
		
		if (marginXY==0 && marginZ==0) {
			return;
		}
		
		bbox.getCrnrMin().setX( bbox.getCrnrMin().getX() - marginXY );
		bbox.getCrnrMin().setY( bbox.getCrnrMin().getY() - marginXY );
		bbox.getCrnrMin().setZ( bbox.getCrnrMin().getZ() - marginZ );
		
		bbox.clipTo(containingScene);
		
		bbox.extnt().setX( bbox.extnt().getX() + (2*marginXY) );
		bbox.extnt().setY( bbox.extnt().getY() + (2*marginXY) );
		bbox.extnt().setZ( bbox.extnt().getZ() + (2*marginZ) );
		
		bbox.clipTo(containingScene);
	}
	
	private static ObjMaskWithPropertiesCollection relTo(ObjMaskCollection in, BoundingBox src ) throws CreateException {
		
		ObjMaskWithPropertiesCollection out = new ObjMaskWithPropertiesCollection();
		
		for( ObjMask om : in ) {
			BoundingBox bboxNew = new BoundingBox( om.getBoundingBox().relPosTo(src), om.getBoundingBox().extnt() );
			ObjMask omNew = new ObjMask(bboxNew, om.binaryVoxelBox().getVoxelBox(), om.getBinaryValues() );
			out.add( new ObjMaskWithProperties(omNew) );
		}
		
		return out;
	}

}
