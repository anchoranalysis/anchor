package org.anchoranalysis.image.feature.objmask;

import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;

/** 
 * Creates some basic objects for tests.
 * 
 * <p>Currently unused, but keep as it will likely be useful in the future</p>
 * */
public class ObjMaskFixture {
	
	public static ObjMask createSmallCornerObj() {
		return new ObjMask(
			new BoundingBox( new Extent(2,3,1) )
		);
	}
}