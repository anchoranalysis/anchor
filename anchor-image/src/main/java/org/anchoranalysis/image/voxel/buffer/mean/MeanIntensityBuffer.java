package org.anchoranalysis.image.voxel.buffer.mean;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;

public abstract class MeanIntensityBuffer<T extends Buffer> {

	private VoxelBox<T> flatVoxelBox;
	private VoxelBox<FloatBuffer> sumVoxelBox;
	private int cntVoxelBox = 0;

	/** Simple constructor since no preprocessing is necessary. */
	public MeanIntensityBuffer( VoxelBoxFactoryTypeBound<T> flatType, Extent srcExtnt ) {
		Extent flattened = srcExtnt.flatten();
		flatVoxelBox = flatType.create(flattened);
		sumVoxelBox = VoxelBoxFactory.instance().getFloat().create(flattened);
	}
		
	public void projectSlice(T pixels) {
		
		int maxIndex = volumeXY();
		for( int i=0; i<maxIndex; i++) {
			processPixel( pixels, i );
    	}
		cntVoxelBox++;
	}
	
	protected abstract void processPixel( T pixels, int index );
	
	/** Increments a particular offset in the sum bufffer by a certain amount */
	protected void incrSumBuffer( int index, int toAdd ) {
		FloatBuffer sumBuffer = sumBuffer();
		sumBuffer.put( index, sumBuffer.get(index) + toAdd );
	}
	
	protected FloatBuffer sumBuffer() {
		return sumVoxelBox.getPixelsForPlane(0).buffer();
	}
	
	protected T flatBuffer() {
		return flatVoxelBox.getPixelsForPlane(0).buffer();
	}
	
	protected int count() {
		return cntVoxelBox;
	}
	
	/** How many pixels in an XY slice */
	protected int volumeXY() {
		return flatVoxelBox.extnt().getVolumeXY();
	}
	
	public VoxelBox<T> getFlatBuffer() {
		return flatVoxelBox;
	}
}
