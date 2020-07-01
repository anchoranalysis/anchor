package org.anchoranalysis.image.objmask.factory.unionfind;

/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import java.nio.Buffer;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxFactory;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.factory.unionfind.ConnectedComponentUnionFind;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;
import org.anchoranalysis.image.voxel.iterator.ObjMaskFixture;

import static org.anchoranalysis.image.voxel.iterator.ObjMaskFixture.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ConnectedComponentUnitFindTest {

	private static final int NUM_NON_OVERLAPPING_OBJS = 5;
	private static final int NUM_OVERLAPPING_OBJS = 3;
	private static final int NUM_OBJS = NUM_NON_OVERLAPPING_OBJS + NUM_OVERLAPPING_OBJS;
	
	/** Used as a positive between non-overlapping objects, or as a negative shift between overlapping objects */
	private static final int DISTANCE_BETWEEN = 10;
		
	private ConnectedComponentUnionFind cc;
	
	@Before
	public void setup() {
		 cc = new ConnectedComponentUnionFind(1, false);
	}
	
	@Test
	public void testByte2d() throws OperationFailedException, CreateException {

		testObjs(
			deriveInt(false),
			ObjMaskFixture.OBJ_NUM_VOXELS_2D
		);
	}
	
	@Test
	public void testInt2d() throws OperationFailedException, CreateException {
		testObjs(
			deriveByte(false),
			ObjMaskFixture.OBJ_NUM_VOXELS_2D
		);
	}
	
	@Test
	public void testByte3d() throws OperationFailedException, CreateException {

		testObjs(
			deriveInt(true),
			ObjMaskFixture.OBJ_NUM_VOXELS_3D
		);
	}
	
	@Test
	public void testInt3d() throws OperationFailedException, CreateException {
		testObjs(
			deriveByte(true),
			ObjMaskFixture.OBJ_NUM_VOXELS_3D
		);
	}
	
	private ObjectCollection deriveInt(boolean do3D) throws OperationFailedException, CreateException {
		return cc.deriveConnectedInt(
			createBufferWithObjs(VoxelDataTypeUnsignedInt.instance, do3D)	
		);
	}
	
	private ObjectCollection deriveByte(boolean do3D) throws OperationFailedException, CreateException {
		return cc.deriveConnectedByte(
			createBufferWithObjs(VoxelDataTypeUnsignedByte.instance, do3D)	
		);
	}
	
	private void testObjs(ObjectCollection objs, int expectedSingleObjSize ) throws CreateException, OperationFailedException {
		assertEquals("number of objects", NUM_NON_OVERLAPPING_OBJS+1, objs.size() );
		assertTrue("size of all objects except one", allSizesEqualExceptOne(objs, expectedSingleObjSize) );
	}
		
	private <T extends Buffer> BinaryVoxelBox<T> createBufferWithObjs( VoxelDataType bufferDataType, boolean do3D ) throws CreateException {
		
		ObjMaskFixture fixture = new ObjMaskFixture(do3D);
		
		Extent extent = new Extent(
			NUM_OBJS * (WIDTH + DISTANCE_BETWEEN),
			NUM_OBJS * (HEIGHT + DISTANCE_BETWEEN),
			DEPTH
		);
		
		@SuppressWarnings("unchecked")
		BinaryVoxelBox<T> bvb = (BinaryVoxelBox<T>) BinaryVoxelBoxFactory.instance().create(extent, bufferDataType, BinaryValues.getDefault());
		
		ObjectCollection objs = createObjs(fixture);
		for(ObjectMask om : objs) {
			bvb.setPixelsCheckMaskOn(om);
		}
		return bvb;
	}
	
	private ObjectCollection createObjs(ObjMaskFixture fixture) {
		Point3i running = new Point3i();
		return ObjectCollectionFactory.from(
			generateObjectsAndIncrementRunning(NUM_NON_OVERLAPPING_OBJS, DISTANCE_BETWEEN, running, fixture),
			generateObjectsAndIncrementRunning(NUM_OVERLAPPING_OBJS, -DISTANCE_BETWEEN, running, fixture)		
		);
	}

	private static ObjectCollection generateObjectsAndIncrementRunning(int numObjs, int shift, Point3i running, ObjMaskFixture fixture) {
		return ObjectCollectionFactory.fromRepeated(
			numObjs,
			() -> {
				ObjectMask mask = fixture.filledMask(running.getX(), running.getY()); 
				running.incrementX(WIDTH + shift);
				running.incrementY(HEIGHT + shift);
				return mask;
			}
		);
	}
	
	/** 
	 * Checks that all objects have a number of voxels exactly equal to target, except one which is allowed to be greater.
	 * 
	 * @param objs objects to check
	 * @parma target size that all objects apart from one should be equal to
	 * */
	private static boolean allSizesEqualExceptOne( ObjectCollection objs, int target ) {
		
		boolean encounteredAlreadyTheException = false;
		
		for( ObjectMask obj : objs ) {
			int numVoxels = obj.numVoxelsOn();
			if (numVoxels==target) {
				continue;
			} else {
				if (numVoxels < target) {
					// At least one LESS than the target
					return false;
				} else {
					if (encounteredAlreadyTheException) {
						// As we've already encountered the exception, then there is more than one exception
						return false;
					} else {
						// The first exception that is encountered
						encounteredAlreadyTheException = true;
					}
				}
			}
		}
		
		// We only fulfill the cteriria if we've encountered the exception
		return encounteredAlreadyTheException;
	}
}
