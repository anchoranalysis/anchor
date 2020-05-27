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
import java.util.Optional;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.chnl.factory.ChnlFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;


/**
 * Outputs a channel but with ONLY the pixels in a mask shown, and others set to 0.
 * 
 * @author owen
 *
 */
public class ChnlMaskedWithObjGenerator extends RasterGenerator implements IterableGenerator<ObjMask> {

	private Chnl srcChnl;
	private ObjMask mask = null;
	
	public ChnlMaskedWithObjGenerator(Chnl srcChnl ) {
		super();
		this.srcChnl = srcChnl;
	}
	
	public ChnlMaskedWithObjGenerator(ObjMask mask, Chnl srcChnl) {
		this.mask = mask;
		this.srcChnl = srcChnl;
	}

	@Override
	public Stack generate() throws OutputWriteFailedException {
		
		if (getIterableElement()==null) {
			throw new OutputWriteFailedException("no mutable element set");
		}
		
		Chnl outChnl = createMaskedChnl( getIterableElement(), (Chnl) srcChnl);
		return new Stack( outChnl );
	}

	@Override
	public ObjMask getIterableElement() {
		return mask;
	}

	@Override
	public void setIterableElement(ObjMask element) {
		mask = element;
	}

	@Override
	public RasterGenerator getGenerator() {
		return this;
	}

	@Override
	public Optional<ManifestDescription> createManifestDescription() {
		return Optional.of(
			new ManifestDescription("raster", "maskChnl")
		);
	}

	@Override
	public boolean isRGB() {
		return false;
	}
	
	/**
	 * Creates a new channel, which copies voxels from srcChnl, but sets voxels outside the mask to 0
	 * 
	 * i.e. the new channel is a masked version of srcChnl
	 * 
	 * @param mask mask
	 * @param srcChnl the channel to copy
	 * @return the masked channel
	 */
	private static Chnl createMaskedChnl( ObjMask mask, Chnl srcChnl) {
		
		BoundingBox bbox = mask.getBoundingBox();
		
		ImageDim newSd = new ImageDim(
			bbox.extent(),
			srcChnl.getDimensions().getRes()
		);
		
		Chnl chnlNew = ChnlFactory.instance().createEmptyInitialised(newSd, srcChnl.getVoxelDataType());
		
		byte maskOn = mask.getBinaryValuesByte().getOnByte();
		
		Point3i maxGlobal = bbox.calcCrnrMax();
		Point3i pntGlobal = new Point3i();
		Point3i pntLocal = new Point3i();
		
		VoxelBox<?> vbSrc = srcChnl.getVoxelBox().any(); 
		VoxelBox<?> vbNew = chnlNew.getVoxelBox().any();
		
		pntLocal.setZ(0);
		for (pntGlobal.setZ(bbox.getCrnrMin().getZ()); pntGlobal.getZ() <=maxGlobal.getZ(); pntGlobal.incrZ(), pntLocal.incrZ()) {
			
			ByteBuffer maskIn = mask.getVoxelBox().getPixelsForPlane(pntLocal.getZ()).buffer();
			VoxelBuffer<?> pixelsIn = vbSrc.getPixelsForPlane(pntGlobal.getZ());
			VoxelBuffer<?> pixelsOut = vbNew.getPixelsForPlane(pntLocal.getZ());
			
			for (pntGlobal.setY(bbox.getCrnrMin().getY()); pntGlobal.getY() <= maxGlobal.getY(); pntGlobal.incrY() ) {
			
				for (pntGlobal.setX(bbox.getCrnrMin().getX()); pntGlobal.getX() <= maxGlobal.getX(); pntGlobal.incrX() ) {	

					if (maskIn.get()!=maskOn) {
						continue;
					}
					
					int indexGlobal = srcChnl.getDimensions().offset(pntGlobal.getX(), pntGlobal.getY());
					pixelsOut.putInt(maskIn.position()-1, pixelsIn.getInt(indexGlobal) );
				}
			}
		}
		
		return chnlNew;
	}

}
