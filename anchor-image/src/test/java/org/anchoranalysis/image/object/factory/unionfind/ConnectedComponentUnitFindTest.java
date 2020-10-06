/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.image.object.factory.unionfind;

import static org.junit.Assert.*;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.voxel.BinaryVoxels;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelsFactory;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFixture;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.ObjectMaskFixture;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.junit.Before;
import org.junit.Test;

public class ConnectedComponentUnitFindTest {

    private ConnectedComponentUnionFind connectedComponents;

    private ObjectCollectionFixture objectsFixture;
    
    @Before
    public void setup() {
        connectedComponents = new ConnectedComponentUnionFind(1, false);
        objectsFixture = new ObjectCollectionFixture();
    }

    @Test
    public void testByte2d() throws OperationFailedException, CreateException {
        testObjects(deriveInt(false), ObjectMaskFixture.OBJECT_NUM_VOXELS_2D);
    }

    @Test
    public void testInt2d() throws OperationFailedException, CreateException {
        testObjects(deriveByte(false), ObjectMaskFixture.OBJECT_NUM_VOXELS_2D);
    }

    @Test
    public void testByte3d() throws OperationFailedException, CreateException {
        testObjects(deriveInt(true), ObjectMaskFixture.OBJECT_NUM_VOXELS_3D);
    }

    @Test
    public void testInt3d() throws OperationFailedException, CreateException {
        testObjects(deriveByte(true), ObjectMaskFixture.OBJECT_NUM_VOXELS_3D);
    }

    private ObjectCollection deriveInt(boolean do3D)
            throws OperationFailedException, CreateException {
        objectsFixture.setDo3D(do3D);
        return connectedComponents.deriveConnectedInt(createBufferWithObjects(UnsignedIntVoxelType.INSTANCE, do3D));
    }

    private ObjectCollection deriveByte(boolean do3D)
            throws OperationFailedException, CreateException {
        return connectedComponents.deriveConnectedByte(
                createBufferWithObjects(UnsignedByteVoxelType.INSTANCE, do3D));
    }

    private void testObjects(ObjectCollection objects, int expectedSingleObjectSize)
            throws CreateException, OperationFailedException {
        assertEquals("number of objects", objectsFixture.getNumberNonOverlappingObjects() + 1, objects.size());
        assertTrue(
                "size of all objects except one",
                allSizesEqualExceptOne(objects, expectedSingleObjectSize));
    }

    private <T> BinaryVoxels<T> createBufferWithObjects(VoxelDataType bufferDataType, boolean do3D)
            throws CreateException {
        
        ObjectCollectionFixture fixture = new ObjectCollectionFixture();
        fixture.setDo3D(do3D);
        
        Extent extent = fixture.extentLargerThanAllObjects();

        @SuppressWarnings("unchecked")
        BinaryVoxels<T> voxels =
                (BinaryVoxels<T>) BinaryVoxelsFactory.createEmptyOff(extent, bufferDataType);

        VoxelsAssigner assigner = voxels.assignOn();
        fixture.createObjects(true).forEach(assigner::toObject);
        return voxels;
    }

    /**
     * Checks that all objects have a number of voxels exactly equal to target, except one which is
     * allowed to be greater.
     *
     * @param objects objects to check
     * @parma target size that all objects apart from one should be equal to
     */
    private static boolean allSizesEqualExceptOne(ObjectCollection objects, int target) {

        boolean encounteredAlreadyTheException = false;

        for (ObjectMask objectMask : objects) {
            int numVoxels = objectMask.numberVoxelsOn();
            if (numVoxels == target) {
                continue;
            } else {
                if (numVoxels < target) {
                    // At least one LESS than the target
                    return false;
                } else {
                    if (encounteredAlreadyTheException) {
                        // As we've already encountered the exception, then there is more than one
                        // exception
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
