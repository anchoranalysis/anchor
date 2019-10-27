package org.anchoranalysis.image.seed;

/*
 * #%L
 * anchor-image
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


import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Tuple3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;

public class SeedsFactory {

	public static Seed create(Point3d point) {
		return new SeedCenterPoint(point);
	}
	
	public static Seed create(Point3d point, BinaryValuesByte bvb) {
		return new SeedCenterPoint(point, bvb);
	}
	
	public static SeedCollection createSeedsWithoutMask( ObjMaskCollection seeds ) throws CreateException {
		// We create a collection of seeds localised appropriately
		// NB: we simply change the object seeds, as it seemingly won't be used again!!!
		SeedCollection seedsObj = new SeedCollection();
		for( ObjMask om : seeds ) {
			
			seedsObj.add(
				createSeed(om)
			);
		}
				
		return seedsObj;
	}
	
	public static SeedCollection createSeedsWithMask(
		ObjMaskCollection seeds,
		ObjMask containingMask,
		Tuple3i subtractFromCrnrMin,
		ImageDim dim
	) throws CreateException {
		// We create a collection of seeds localised appropriately
		// NB: we simply change the object seeds, as it seemingly won't be used again!!!
		SeedCollection seedsObj = new SeedCollection();
		for( ObjMask om : seeds ) {
			seedsObj.add(
				createSeedWithinMask(
					om,
					containingMask.getBoundingBox(),
					subtractFromCrnrMin,
					dim
				)
			);
		}
		
		try {
			seedsObj.verifySeedsAreInside( containingMask.getBoundingBox().extnt() );
		} catch (OperationFailedException e) {
			throw new CreateException(e);
		}
		
		return seedsObj;
	}
	
	private static SeedObjMask createSeed( ObjMask om ) {
		return new SeedObjMask(
			om.duplicate()
		);
	}
	
	private static SeedObjMask createSeedWithinMask( ObjMask om, BoundingBox containingBBox, Tuple3i subtractFromCrnrMin, ImageDim dim ) throws CreateException {
		ObjMask omSeedDup = om.duplicate();
		omSeedDup.getBoundingBox().getCrnrMin().sub( subtractFromCrnrMin );
		
		// If a seed object is partially located outside an object, the above line might fail, so we should test
		if (!containingBBox.contains( omSeedDup.getBoundingBox())) {
			
			// We only take the part of the seed object that intersects with our bbox
			BoundingBox bboxIntersect = containingBBox.intersectCreateNew( omSeedDup.getBoundingBox(), dim.getExtnt() );
			assert( bboxIntersect!=null );
			omSeedDup = omSeedDup.createSubmaskAlwaysNew(bboxIntersect);
		}
		return new SeedObjMask(omSeedDup);
	}
}
