package org.anchoranalysis.anchor.mpp.probmap;

import org.anchoranalysis.anchor.mpp.mark.set.UpdatableMarkSet;

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
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.objmask.ops.BinaryChnlFromObjs;

public class ProbMapObjMaskCollection extends ProbMap {

	private ObjMaskCollection objMaskCollection;
	private ImageDim dim;
	
	private ProbWeights probWeights;
	
	public ProbMapObjMaskCollection(ObjMaskCollection objMaskCollection,
			ImageDim dim) throws CreateException {
		super();
		this.objMaskCollection = objMaskCollection;
		this.dim = dim;
		
		
		probWeights = new ProbWeights();
		for( ObjMask om : objMaskCollection ) {
			probWeights.add( (double) om.binaryVoxelBox().countOn() );
		}
	}

	private Point3d sampleFromObjMask( ObjMask om, RandomNumberGenerator re ) {

		// Now we keep picking a pixel at random from the object mask until we find one that is
		//  on.  Could be very inefficient for low-density bounding boxes? So we should make sure
		//  bounding boxes are tight
		
		long vol = om.getVoxelBox().extnt().getVolume();
		int volXY = om.getVoxelBox().extnt().getVolumeXY();
		int exY = om.getVoxelBox().extnt().getX();
		
		while(true) {
			
			int index3D = (int) (re.nextDouble() * vol);
			
			int slice = index3D / volXY;
			int index2D = index3D % volXY;
			
			byte b = om.getVoxelBox().getPixelsForPlane(slice).buffer().get(index2D);
			if (b==om.getBinaryValuesByte().getOnByte()) {
				
				int xRel = index2D%exY;
				int yRel = index2D/exY;
				int zRel = slice;
				
				int x = xRel + om.getBoundingBox().getCrnrMin().getX();
				int y = yRel + om.getBoundingBox().getCrnrMin().getY();
				int z = zRel + om.getBoundingBox().getCrnrMin().getZ();
				
				return new Point3d( x, y, z );
			}
		}
	}
	
	@Override
	public Point3d sample(RandomNumberGenerator re) {
		
		if (probWeights.size()==0) {
			return null;
		}
		
		// We want to pick an ObjMaskCollection sampling, by picking one of the obj masks
		//   weighing by the number of on pixels
		int index = probWeights.sample(re);
		
		assert(index>=0);
		
		ObjMask om = objMaskCollection.get(index);
		return sampleFromObjMask(om,re);
	}

	@Override
	public ImageDim getDimensions() {
		return dim;
	}

	@Override
	public BinaryChnl visualization()
			throws OptionalOperationUnsupportedException {
		try {
			return BinaryChnlFromObjs.createFromObjs(
				objMaskCollection,
				dim,
				BinaryValues.getDefault()
			);
		} catch (CreateException e) {
			assert false;
			return null;
		}
	}

	@Override
	public UpdatableMarkSet updater() {
		return null;
	}

}
