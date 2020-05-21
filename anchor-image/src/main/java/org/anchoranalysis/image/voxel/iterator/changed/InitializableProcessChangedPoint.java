package org.anchoranalysis.image.voxel.iterator.changed;

import org.anchoranalysis.core.geometry.Point3i;

public interface InitializableProcessChangedPoint extends ProcessChangedPoint {

	void initPnt(Point3i pnt);
}
