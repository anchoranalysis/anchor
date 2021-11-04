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

package org.anchoranalysis.image.voxel.datatype;

import lombok.AllArgsConstructor;

/**
 * Multiplexes between factories for the various {@link VoxelDataType}s.
 *
 * @author Owen Feehan
 * @param <T> type of factory provided for a particular voxel-data-type
 */
@AllArgsConstructor
public abstract class VoxelDataTypeFactoryMultiplexer<T> {

    private T factoryUnsignedByte;
    private T factoryUnsignedShort;
    private T factoryUnsignedInt;
    private T factoryFloat;

    /**
     * Multiplexes one of the factories according to data-type
     *
     * @param dataType the type to find a factory for
     * @return a factory if it exists, or else an exception
     */
    public T get(VoxelDataType dataType) {

        if (dataType.equals(UnsignedByteVoxelType.INSTANCE)) {
            return factoryUnsignedByte;
        } else if (dataType.equals(UnsignedShortVoxelType.INSTANCE)) {
            return factoryUnsignedShort;
        } else if (dataType.equals(UnsignedIntVoxelType.INSTANCE)) {
            return factoryUnsignedInt;
        } else if (dataType.equals(FloatVoxelType.INSTANCE)) {
            return factoryFloat;
        } else {
            throw new IncorrectVoxelTypeException("Non-existent type");
        }
    }
}
