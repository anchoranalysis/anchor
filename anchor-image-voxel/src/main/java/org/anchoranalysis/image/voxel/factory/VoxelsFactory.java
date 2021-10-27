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
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.buffer.slice.SliceBufferIndex;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFactoryMultiplexer;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Creates {@link VoxelsUntyped} and provides a singleton location for implementations of {@link VoxelsFactoryTypeBound} for different types.
 * 
 * @author Owen Feehan
 *
 */
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

    /** 
     * Singleton instance.
     *
     * @return a single instance of this class.
     */
    public static VoxelsFactory instance() {
        if (instance == null) {
            instance = new VoxelsFactory();
        }
        return instance;
    }

    /**
     * Creates voxels from a particular {@link SliceBufferIndex} with specified type.
     * 
     * @param <T> the buffer-type to use in the voxels.
     * @param buffer the buffer to create a {@link VoxelsUntyped} from.
     * @param dataType the data-type that should be compatible with {@code T}.
     * @return a newly created {@link VoxelsUntyped} that reuses the memory in {@code buffer}.
     */
    public <T> VoxelsUntyped createFrom(SliceBufferIndex<T> buffer, VoxelDataType dataType) {
        @SuppressWarnings("unchecked")
        VoxelsFactoryTypeBound<T> factory = (VoxelsFactoryTypeBound<T>) get(dataType);
        Voxels<T> voxels = factory.create(buffer);
        return new VoxelsUntyped(voxels);
    }
    
    /**
     * Creates empty voxels to match a particular size.
     * 
     * @param extent the size of the {@link VoxelsUntyped} to create.
     * @param dataType the voxel data-type to create.
     * @return the created voxels.
     */
    public VoxelsUntyped createEmpty(Extent extent, VoxelDataType dataType) {
        VoxelsFactoryTypeBound<?> factory = get(dataType);
        Voxels<?> buffer = factory.createInitialized(extent);
        return new VoxelsUntyped(buffer);
    }

    /**
     * A factory that creates voxels of type <i>unsigned byte</i>.
     * 
     * @return the corresponding factory.
     */
    public static VoxelsFactoryTypeBound<UnsignedByteBuffer> getUnsignedByte() {
        return FACTORY_UNSIGNED_BYTE;
    }

    /**
     * A factory that creates voxels of type <i>unsigned short</i>.
     * 
     * @return the corresponding factory.
     */
    public static VoxelsFactoryTypeBound<UnsignedShortBuffer> getUnsignedShort() {
        return FACTORY_UNSIGNED_SHORT;
    }

    /**
     * A factory that creates voxels of type <i>unsigned int</i>.
     * 
     * @return the corresponding factory.
     */
    public static VoxelsFactoryTypeBound<UnsignedIntBuffer> getUnsignedInt() {
        return FACTORY_UNSIGNED_INT;
    }

    /**
     * A factory that creates voxels of type <i>float</i>.
     * 
     * @return the corresponding factory.
     */
    public static VoxelsFactoryTypeBound<FloatBuffer> getFloat() {
        return FACTORY_FLOAT;
    }
}
