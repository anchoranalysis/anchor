package org.anchoranalysis.image.voxel.iterator.changed;

public interface InitializableProcessChangedPoint extends ProcessChangedPoint {

	void initPnt( int pntX, int pntY, int pntZ );
}
