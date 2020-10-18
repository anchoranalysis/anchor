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

package org.anchoranalysis.image.voxel.factory;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFactoryMultiplexer;
import org.anchoranalysis.image.voxel.factory.sliceindex.SliceBufferIndex;
import org.anchoranalysis.spatial.extent.Extent;

public class VoxelsFactory extends VoxelDataTypeFactoryMultiplexer<VoxelsFactoryTypeBound<?>> {

    // Singleton
    private static VoxelsFactory instance;

    private static final VoxelsFactoryTypeBound<UnsignedByteBuffer> FACTORY_UNSIGNED_BYTE =
            new FactoryUnsignedByte();
    private static final VoxelsFactoryTypeBound<UnsignedShortBuffer> FACTORY_UNSIGNED_SHORT =
            new FactoryUnsignedShort();
    private static final VoxelsFactoryTypeBound<UnsignedIntBuffer> FACTORY_UNSIGNED_INT =
            new FactoryUnsignedInt();
    private static final VoxelsFactoryTypeBound<FloatBuffer> FACTORY_FLOAT =
            new FactoryUnsignedFloat();

    private VoxelsFactory() {
        super(FACTORY_UNSIGNED_BYTE, FACTORY_UNSIGNED_SHORT, FACTORY_UNSIGNED_INT, FACTORY_FLOAT);
    }

    /** Singleton */
    public static VoxelsFactory instance() {
        if (instance == null) {
            instance = new VoxelsFactory();
        }
        return instance;
    }

    public <T> VoxelsWrapper create(SliceBufferIndex<T> pixelsForPlane, VoxelDataType dataType) {
        @SuppressWarnings("unchecked")
        VoxelsFactoryTypeBound<T> factory = (VoxelsFactoryTypeBound<T>) get(dataType);
        Voxels<T> buffer = factory.create(pixelsForPlane);
        return new VoxelsWrapper(buffer);
    }

    public VoxelsWrapper create(Extent e, VoxelDataType dataType) {
        VoxelsFactoryTypeBound<?> factory = get(dataType);
        Voxels<?> buffer = factory.createInitialized(e);
        return new VoxelsWrapper(buffer);
    }

    public static VoxelsFactoryTypeBound<UnsignedByteBuffer> getUnsignedByte() {
        return FACTORY_UNSIGNED_BYTE;
    }

    public static VoxelsFactoryTypeBound<UnsignedShortBuffer> getUnsignedShort() {
        return FACTORY_UNSIGNED_SHORT;
    }

    public static VoxelsFactoryTypeBound<UnsignedIntBuffer> getUnsignedInt() {
        return FACTORY_UNSIGNED_INT;
    }

    public static VoxelsFactoryTypeBound<FloatBuffer> getFloat() {
        return FACTORY_FLOAT;
    }
}
