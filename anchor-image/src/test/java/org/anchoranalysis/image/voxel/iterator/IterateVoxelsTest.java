package org.anchoranalysis.image.voxel.iterator;

import static org.junit.Assert.*;

import java.util.function.Consumer;
import java.nio.ByteBuffer;
import java.util.Optional;

import org.anchoranalysis.core.arithmetic.RunningSum;
import org.anchoranalysis.core.arithmetic.RunningSumPoint;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.chnl.factory.ChnlFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;
import org.junit.Test;

public class IterateVoxelsTest {

	/** START: Constants for object sizes and locations */
	private static final int WIDTH = 40;
	private static final int HEIGHT = 50;
	
	private static final int VOXELS_REMOVED_CORNERS = 4;
	private static final int BOUNDING_BOX_NUM_VOXELS = WIDTH * HEIGHT;
	private static final int OBJ_NUM_VOXELS = BOUNDING_BOX_NUM_VOXELS - VOXELS_REMOVED_CORNERS;
	
	private static final int Y_MASK_1 = 30;
	private static final int Y_MASK_2 = 35;
	/** END: Constants for object sizes and locations */
		
	private static class CountPoints implements ProcessPoint {

		private RunningSumPoint runningSum = new RunningSumPoint();
		
		@Override
		public void process(Point3i pnt) {
			runningSum.increment(pnt);
		}

		public int count() {
			return runningSum.getCountXY();
		}

		/** The center-of-gravity of all points-processed (discretized) */
		public Point3i center() {
			return new Point3i( runningSum.mean() );
		}
	}
	
	@Test
	public void test() {

		ObjMask mask1 = filledMask(20, Y_MASK_1);
		ObjMask mask2 = filledMask(20, Y_MASK_2);	// Overlaps with mask1 but not entirely
		
		testSingleMask("mask1", OBJ_NUM_VOXELS, mask1);
		testSingleMask("mask2", OBJ_NUM_VOXELS, mask2);
		testIntersectionMasks(
			"intersection",
			OBJ_NUM_VOXELS - ((Y_MASK_2-Y_MASK_1)*WIDTH),
			new Point3i(39, 57, 0),
			mask1,
			mask2
		);
		testBoundingBox("bbox1", mask1.getBoundingBox());
		testBoundingBox("bbox2", mask2.getBoundingBox());
		
	}
	
	private void testSingleMask(String message, int expectedNumVoxels, ObjMask mask) {
		testCounter(
			message,
			expectedNumVoxels,
			mask.getBoundingBox().centerOfGravity(),
			counter -> IterateVoxels.callEachPoint(mask, counter)
		);
	}
	
	private void testIntersectionMasks(String message, int expectedNumVoxels, Point3i expectedCenter, ObjMask mask1, ObjMask mask2) {
		testCounter(
			message,
			expectedNumVoxels,
			expectedCenter,
			counter -> IterateVoxels.overMasks(mask1, Optional.of(mask2), counter)
		);
	}
	
	private void testBoundingBox(String message, BoundingBox box ) {
		testCounter(
			message,
			box.extnt().getVolume(),
			box.centerOfGravity(),
			counter -> IterateVoxels.callEachPoint(box, counter)
		);
	}
	
	private void testCounter(String message, int expectedNumVoxels, Point3i expectedCenter, Consumer<CountPoints> func) {
		CountPoints counter = new CountPoints();
		func.accept(counter);
		assertEquals(message + " count", expectedNumVoxels, counter.count());
		assertEquals(message + " center", expectedCenter, counter.center() );
	}
	
	private static ObjMask filledMask( int crnrX, int crnrY) {
		return filledMask(crnrX, crnrY, WIDTH, HEIGHT);
	}
	
	/** A rectangular mask with single-pixel corners removed */
	private static ObjMask filledMask( int crnrX, int crnrY, int width, int height ) {
		Point3i crnr = new Point3i(crnrX, crnrY, 0);
		Extent extent = new Extent(width, height, 1);
		
		ObjMask om = new ObjMask(
			new BoundingBox(crnr, extent)
		);
		om.binaryVoxelBox().setAllPixelsToOn();
		removeEachCorner(om);
		return om;
	}
	
	private static void removeEachCorner( ObjMask mask ) {
		
		BinaryVoxelBox<ByteBuffer> bvb = mask.binaryVoxelBox();
		
		Extent e = mask.getBoundingBox().extnt();
		int widthMinusOne = e.getX() - 1;
		int heightMinusOne = e.getY() - 1;
		
		bvb.setLow(0, 0, 0);
		bvb.setLow(widthMinusOne, 0, 0);
		bvb.setLow(0, heightMinusOne, 0);
		bvb.setLow(widthMinusOne, heightMinusOne, 0);
	}
}
