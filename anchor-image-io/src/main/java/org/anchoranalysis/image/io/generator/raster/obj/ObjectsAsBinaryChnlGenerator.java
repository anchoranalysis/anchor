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
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

import lombok.RequiredArgsConstructor;

/**
 * Writes an object-mask as a channel
 * @author Owen Feehan
 *
 */
@RequiredArgsConstructor
public class ObjectsAsBinaryChnlGenerator extends RasterGenerator implements IterableGenerator<ObjectMask> {
	
	// START REQUIRED ARGUMENTS
	private final int maskVal;
	private final ImageResolution res;
	// END REQUIRED ARGUMENTS
	
	private ObjectMask mask;

	public ObjectsAsBinaryChnlGenerator(ObjectMask mask, int maskVal, ImageResolution dim) {
		this.mask = mask;
		this.maskVal = maskVal;
		this.res = dim;
	}

	@Override
	public Stack generate() throws OutputWriteFailedException {
		
		if (getIterableElement()==null) {
			throw new OutputWriteFailedException("no mutable element set");
		}
		
		Channel outChnl = createChnlFromMask( getIterableElement(), this.res, maskVal);
		
		return new Stack( outChnl );
	}


	@Override
	public ObjectMask getIterableElement() {
		return this.mask;
	}


	@Override
	public void setIterableElement(ObjectMask element) {
		this.mask = element;
	}


	@Override
	public Generator getGenerator() {
		return this;
	}

	@Override
	public Optional<ManifestDescription> createManifestDescription() {
		return Optional.of(
			new ManifestDescription("raster", "mask")
		);
	}

	@Override
	public boolean isRGB() {
		return false;
	}

	private static Channel createChnlFromMask( ObjectMask mask, ImageResolution res, int chnlOutVal ) {
		
		BoundingBox bbox = mask.getBoundingBox();
		
		ImageDimensions newSd = new ImageDimensions(
			bbox.extent(),
			res
		);
				
		Channel chnlNew = ChannelFactory.instance().createEmptyInitialised( newSd, VoxelDataTypeUnsignedByte.INSTANCE );
		
		VoxelBox<ByteBuffer> vbNew = chnlNew.getVoxelBox().asByte();
		
		byte maskVal = mask.getBinaryValuesByte().getOnByte();
		byte chnlOutValByte = (byte) chnlOutVal;
		
		Point3i pointLocal = new Point3i();
		
		for (pointLocal.setZ(0); pointLocal.getZ() < newSd.getZ(); pointLocal.incrementZ()) {
			
			ByteBuffer pixelsIn = mask.getVoxelBox().getPixelsForPlane(pointLocal.getZ()).buffer();
			ByteBuffer pixelsOut = vbNew.getPlaneAccess().getPixelsForPlane(pointLocal.getZ()).buffer();
			
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
