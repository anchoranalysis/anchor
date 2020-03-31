package org.anchoranalysis.image.io.generator.raster.obj;

/*-
 * #%L
 * anchor-image-io
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

import java.nio.ByteBuffer;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.chnl.factory.ChnlFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Writes an object-mask as a channel
 * @author owen
 *
 */
public class ObjAsBinaryChnlGenerator extends RasterGenerator implements IterableGenerator<ObjMask> {
	
	private int maskVal;
	private ImageRes res;
	private ObjMask mask;
	
	public ObjAsBinaryChnlGenerator(int maskVal, ImageRes res) {
		this.maskVal = maskVal;
		this.res = res;
	}

	public ObjAsBinaryChnlGenerator(ObjMask mask, int maskVal, ImageRes dim) {
		this.mask = mask;
		this.maskVal = maskVal;
		this.res = dim;
	}

	@Override
	public Stack generate() throws OutputWriteFailedException {
		
		if (getIterableElement()==null) {
			throw new OutputWriteFailedException("no mutable element set");
		}
		
		Chnl outChnl = createChnlFromMask( getIterableElement(), this.res, maskVal);
		
		return new Stack( outChnl );
	}


	@Override
	public ObjMask getIterableElement() {
		return this.mask;
	}


	@Override
	public void setIterableElement(ObjMask element) {
		this.mask = element;
	}


	@Override
	public Generator getGenerator() {
		return this;
	}

	@Override
	public ManifestDescription createManifestDescription() {
		return new ManifestDescription("raster", "mask");
	}
	
	@Override
	public void start() throws OutputWriteFailedException {
	}


	@Override
	public void end() throws OutputWriteFailedException {
	}

	@Override
	public boolean isRGB() {
		return false;
	}

	private static Chnl createChnlFromMask( ObjMask mask, ImageRes res, int chnlOutVal ) {
		
		BoundingBox bbox = mask.getBoundingBox();
		
		ImageDim newSd = new ImageDim();
		newSd.setRes( res );
		newSd.setX( bbox.extnt().getX() );
		newSd.setY( bbox.extnt().getY() );
		newSd.setZ( bbox.extnt().getZ() );
		
				
		Chnl chnlNew = ChnlFactory.instance().createEmptyInitialised( newSd, VoxelDataTypeUnsignedByte.instance );
		
		VoxelBox<ByteBuffer> vbNew = chnlNew.getVoxelBox().asByte();
		
		byte maskVal = mask.getBinaryValuesByte().getOnByte();
		byte chnlOutValByte = (byte) chnlOutVal;
		
		Point3i pntLocal = new Point3i();
		
		for (pntLocal.setZ(0); pntLocal.getZ() < newSd.getZ(); pntLocal.incrZ()) {
			
			ByteBuffer pixelsIn = mask.getVoxelBox().getPixelsForPlane(pntLocal.getZ()).buffer();
			ByteBuffer pixelsOut = vbNew.getPlaneAccess().getPixelsForPlane(pntLocal.getZ()).buffer();
			
			while (pixelsIn.hasRemaining()) {
				
				if (pixelsIn.get()!=maskVal) {
					continue;
				}
					
				pixelsOut.put(pixelsIn.position()-1,chnlOutValByte);
			}
		}

		
		return chnlNew;
	}
}
