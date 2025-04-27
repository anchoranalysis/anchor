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

package org.anchoranalysis.image.voxel.binary.connected;

import static org.junit.jupiter.api.Assertions.*;

import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxelsFactory;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectCollectionFixture;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.Extent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConnectedComponentUnitFindTest {

    private ConnectedComponentUnionFind connectedComponents;

    @BeforeEach
    void setup() {
        connectedComponents = new ConnectedComponentUnionFind(1, false);
    }

    @Test
    void testByte2d() throws OperationFailedException, CreateException {
        ObjectCollectionFixture fixture = new ObjectCollectionFixture();
        testObjects(deriveInt(fixture), fixture);
    }

    @Test
    void testInt2d() throws OperationFailedException, CreateException {
        ObjectCollectionFixture fixture = new ObjectCollectionFixture();
        testObjects(deriveByte(fixture), fixture);
    }

    @Test
    void testByte3d() throws OperationFailedException, CreateException {
        ObjectCollectionFixture fixture = new ObjectCollectionFixture();
        fixture.setUseZ(true);
        testObjects(deriveInt(fixture), fixture);
    }

    @Test
    void testInt3d() throws OperationFailedException, CreateException {
        ObjectCollectionFixture fixture = new ObjectCollectionFixture();
        fixture.setUseZ(true);
        testObjects(deriveByte(fixture), fixture);
    }

    private ObjectCollection deriveInt(ObjectCollectionFixture fixture) throws CreateException {
        return connectedComponents.deriveConnectedInt(
                createBufferWithObjects(UnsignedIntVoxelType.INSTANCE, fixture));
    }

    private ObjectCollection deriveByte(ObjectCollectionFixture fixture) throws CreateException {
        return connectedComponents.deriveConnectedByte(
                createBufferWithObjects(UnsignedByteVoxelType.INSTANCE, fixture));
    }

    private void testObjects(ObjectCollection objects, ObjectCollectionFixture fixture)
            throws CreateException, OperationFailedException {
        int expectedSingleObjectSize = expectedSingleNumberVoxels(fixture);
        assertEquals(fixture.getNumberNonOverlapping() + 1, objects.size(), "number of objects");
        assertTrue(
                allSizesEqualExceptOne(objects, expectedSingleObjectSize),
                "size of all objects except one");
    }

    private static int expectedSingleNumberVoxels(ObjectCollectionFixture fixture) {
        return fixture.createObjects(true).get(0).numberVoxelsOn();
    }

    private <T> BinaryVoxels<T> createBufferWithObjects(
            VoxelDataType bufferDataType, ObjectCollectionFixture fixture) throws CreateException {

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
            int numberVoxels = objectMask.numberVoxelsOn();
            if (numberVoxels == target) {
                continue;
            } else {
                if (numberVoxels < target) {
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
