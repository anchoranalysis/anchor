/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2025 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.voxel.buffer;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.junit.jupiter.api.Test;

class VoxelBufferFloatTest {

    @Test
    void testCreateFromFloatBuffer() {
        // Create a FloatBuffer with some test data
        FloatBuffer floatBuffer = FloatBuffer.allocate(5);
        floatBuffer.put(new float[] {1.0f, 2.5f, 3.7f, 4.2f, 5.9f});
        floatBuffer.flip();

        // Create a VoxelBufferFloat from the FloatBuffer
        VoxelBufferFloat voxelBuffer = new VoxelBufferFloat(floatBuffer);

        // Check that the buffer() method returns the original FloatBuffer
        assertSame(floatBuffer, voxelBuffer.buffer());

        // Check that the capacity is correct
        assertEquals(5, voxelBuffer.capacity());

        // Check that the data type is correct
        assertEquals(FloatVoxelType.INSTANCE, voxelBuffer.dataType());

        // Check that we can retrieve values correctly
        assertEquals(1, voxelBuffer.getInt(0));
        assertEquals(2, voxelBuffer.getInt(1));
        assertEquals(3, voxelBuffer.getInt(2));
        assertEquals(4, voxelBuffer.getInt(3));
        assertEquals(5, voxelBuffer.getInt(4));

        // Check that we can set values correctly
        voxelBuffer.putInt(2, 10);
        assertEquals(10, voxelBuffer.getInt(2));

        // Check that the underlying FloatBuffer was modified
        assertEquals(10.0f, floatBuffer.get(2), 0.001f);
    }

    @Test
    void testGetInt() {
        // Create a FloatBuffer with test data
        FloatBuffer floatBuffer = FloatBuffer.allocate(3);
        floatBuffer.put(new float[] {1.7f, -2.3f, 3.5f});
        floatBuffer.flip();

        // Create a VoxelBufferFloat from the FloatBuffer
        VoxelBufferFloat voxelBuffer = new VoxelBufferFloat(floatBuffer);

        // Test getInt() method
        assertEquals(1, voxelBuffer.getInt(0), "Should correctly convert and return 1.7f as 1");
        assertEquals(-2, voxelBuffer.getInt(1), "Should correctly convert and return -2.3f as -2");
        assertEquals(3, voxelBuffer.getInt(2), "Should correctly convert and return 3.5f as 3");
    }

    @Test
    void testPutInt() {
        // Create a FloatBuffer with test data
        FloatBuffer floatBuffer = FloatBuffer.allocate(3);
        floatBuffer.put(new float[] {1.0f, 2.0f, 3.0f});
        floatBuffer.flip();

        // Create a VoxelBufferFloat from the FloatBuffer
        VoxelBufferFloat voxelBuffer = new VoxelBufferFloat(floatBuffer);

        // Test putInt() method
        voxelBuffer.putInt(1, 10);

        // Check that the value was correctly stored
        assertEquals(
                10.0f,
                voxelBuffer.buffer().get(1),
                0.001f,
                "Should correctly store the int value 10 as a float");

        // Check that other values remain unchanged
        assertEquals(1.0f, voxelBuffer.buffer().get(0), 0.001f, "Should not modify other values");
        assertEquals(3.0f, voxelBuffer.buffer().get(2), 0.001f, "Should not modify other values");
    }

    @Test
    void testPutByte() {
        // Create a FloatBuffer with test data
        FloatBuffer floatBuffer = FloatBuffer.allocate(3);
        floatBuffer.put(new float[] {1.0f, 2.0f, 3.0f});
        floatBuffer.flip();

        // Create a VoxelBufferFloat from the FloatBuffer
        VoxelBufferFloat voxelBuffer = new VoxelBufferFloat(floatBuffer);

        // Test putByte() method
        byte byteValue = (byte) 255; // 255 unsigned byte = 255 int
        voxelBuffer.putByte(1, byteValue);

        // Check that the value was correctly stored as a float
        assertEquals(
                255.0f,
                voxelBuffer.buffer().get(1),
                0.001f,
                "Should correctly store the unsigned byte value 255 as a float");

        // Check that other values remain unchanged
        assertEquals(1.0f, voxelBuffer.buffer().get(0), 0.001f, "Should not modify other values");
        assertEquals(3.0f, voxelBuffer.buffer().get(2), 0.001f, "Should not modify other values");
    }

    @Test
    void testCapacity() {
        // Create a FloatBuffer with a specific capacity
        int expectedCapacity = 100;
        FloatBuffer floatBuffer = FloatBuffer.allocate(expectedCapacity);

        // Create a VoxelBufferFloat from the FloatBuffer
        VoxelBufferFloat voxelBuffer = new VoxelBufferFloat(floatBuffer);

        // Check that the capacity method returns the correct value
        assertEquals(
                expectedCapacity,
                voxelBuffer.capacity(),
                "Should return the correct capacity of the underlying FloatBuffer");
    }

    @Test
    void testDataType() {
        FloatBuffer floatBuffer = FloatBuffer.allocate(10);
        VoxelBufferFloat voxelBuffer = new VoxelBufferFloat(floatBuffer);
        assertEquals(
                FloatVoxelType.INSTANCE,
                voxelBuffer.dataType(),
                "Should return FloatVoxelType.INSTANCE");
    }
}
