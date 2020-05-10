package org.anchoranalysis.image.objmask.factory.unionfind;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;

class BoundingBoxWithCount {

	private static Extent extnt1 = new Extent(1,1,1);
	
	private BoundingBox BoundingBox;
	private int cnt = 0;
	
	public void add( Point3i pnt ) {
		if (BoundingBox==null) {
			BoundingBox = new BoundingBox( pnt, extnt1 );
		} else {
			BoundingBox.add(pnt);
		}
		cnt++;
	}

	public int getCnt() {
		return cnt;
	}

	public BoundingBox getBoundingBox() {
		return BoundingBox;
	}
}