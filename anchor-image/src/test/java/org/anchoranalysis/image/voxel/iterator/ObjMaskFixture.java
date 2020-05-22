package org.anchoranalysis.image.voxel.iterator;

import java.nio.ByteBuffer;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;

/**
 * Creates object-masks of a certain shape
 * 
 * @author Owen Feehan
 *
 */
public class ObjMaskFixture {
	
	public static final int WIDTH = 40;
	public static final int HEIGHT = 50;
	public static final int DEPTH = 15;
	
	public static final int VOXELS_REMOVED_CORNERS = 4;
	public static final int BOUNDING_BOX_NUM_VOXELS = WIDTH * HEIGHT;
	public static final int OBJ_NUM_VOXELS_2D = BOUNDING_BOX_NUM_VOXELS - VOXELS_REMOVED_CORNERS;
	public static final int OBJ_NUM_VOXELS_3D = OBJ_NUM_VOXELS_2D * ObjMaskFixture.DEPTH;
	
	private boolean do3D;
	
	public ObjMaskFixture(boolean do3D) {
		this.do3D = do3D;
	}

	public ObjMask filledMask( int crnrX, int crnrY) {
		return filledMask(crnrX, crnrY, WIDTH, HEIGHT);
	}
	
	/** A rectangular mask with single-pixel corners removed */
	public ObjMask filledMask( int crnrX, int crnrY, int width, int height ) {
		Point3i crnr = new Point3i(crnrX, crnrY, 0);
		Extent extent = new Extent(
			width,
			height,
			do3D ? DEPTH : 1
		);
		
		ObjMask om = new ObjMask(
			new BoundingBox(crnr, extent)
		);
		om.binaryVoxelBox().setAllPixelsToOn();
		removeEachCorner(om);
		return om;
	}
	
	private void removeEachCorner( ObjMask mask ) {
		
		BinaryVoxelBox<ByteBuffer> bvb = mask.binaryVoxelBox();
		
		Extent e = mask.getBoundingBox().extnt();
		int widthMinusOne = e.getX() - 1;
		int heightMinusOne = e.getY() - 1;
		
		for( int z=0; z<e.getZ(); z++) {
			bvb.setLow(0, 0, z);
			bvb.setLow(widthMinusOne, 0, z);
			bvb.setLow(0, heightMinusOne, z);
			bvb.setLow(widthMinusOne, heightMinusOne, z);
		}
	}
}
