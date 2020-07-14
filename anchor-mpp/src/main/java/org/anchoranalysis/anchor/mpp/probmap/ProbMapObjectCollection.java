package org.anchoranalysis.anchor.mpp.probmap;

import java.util.Optional;



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

import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.ops.BinaryChnlFromObjects;

public class ProbMapObjectCollection implements ProbMap {

	private final ObjectCollection objects;
	private final ImageDimensions dim;
	
	private final ProbWeights probWeights;
	
	public ProbMapObjectCollection(ObjectCollection objects, ImageDimensions dim) {
		super();
		this.objects = objects;
		this.dim = dim;
		
		
		probWeights = new ProbWeights();
		for( ObjectMask objectMask : objects ) {
			probWeights.add( (double) objectMask.binaryVoxelBox().countOn() );
		}
	}

	@Override
	public Optional<Point3d> sample(RandomNumberGenerator re) {
		
		if (probWeights.size()==0) {
			return Optional.empty();
		}
		
		// We want to pick an ObjMaskCollection sampling, by picking one of the obj masks
		//   weighing by the number of on pixels
		int index = probWeights.sample(re);
		
		assert(index>=0);
		
		ObjectMask object = objects.get(index);
		return Optional.of(
			sampleFromObjMask(object,re)
		);
	}

	@Override
	public ImageDimensions getDimensions() {
		return dim;
	}

	@Override
	public BinaryChnl visualization() throws OptionalOperationUnsupportedException {
		return BinaryChnlFromObjects.createFromObjects(
			objects,
			dim,
			BinaryValues.getDefault()
		);
	}

	private Point3d sampleFromObjMask( ObjectMask object, RandomNumberGenerator re ) {

		// Now we keep picking a pixel at random from the object mask until we find one that is
		//  on.  Could be very inefficient for low-density bounding boxes? So we should make sure
		//  bounding boxes are tight
		
		long vol = object.getVoxelBox().extent().getVolume();
		int volXY = object.getVoxelBox().extent().getVolumeXY();
		int exY = object.getVoxelBox().extent().getX();
		
		while(true) {
			
			int index3D = (int) (re.nextDouble() * vol);
			
			int slice = index3D / volXY;
			int index2D = index3D % volXY;
			
			byte b = object.getVoxelBox().getPixelsForPlane(slice).buffer().get(index2D);
			if (b==object.getBinaryValuesByte().getOnByte()) {
				
				int xRel = index2D%exY;
				int yRel = index2D/exY;
				int zRel = slice;
				
				int x = xRel + object.getBoundingBox().cornerMin().getX();
				int y = yRel + object.getBoundingBox().cornerMin().getY();
				int z = zRel + object.getBoundingBox().cornerMin().getZ();
				
				return new Point3d( x, y, z );
			}
		}
	}
}
