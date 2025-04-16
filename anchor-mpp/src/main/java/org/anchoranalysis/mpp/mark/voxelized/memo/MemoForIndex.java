/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.mark.voxelized.memo;

/**
 * An interface for accessing voxelized mark memos by index.
 *
 * <p>This interface provides methods to retrieve a {@link VoxelizedMarkMemo} for a given index and
 * to get the total number of memos available. It's useful for collections or caches of voxelized
 * mark memos that need to be accessed by their position in the collection.
 */
public interface MemoForIndex {

    /**
     * Retrieves the {@link VoxelizedMarkMemo} for the specified index.
     *
     * @param index the index of the desired memo, must be non-negative and less than {@link
     *     #size()}
     * @return the {@link VoxelizedMarkMemo} corresponding to the given index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    VoxelizedMarkMemo getMemoForIndex(int index);

    /**
     * Returns the total number of memos available.
     *
     * @return the number of memos, always non-negative
     */
    int size();
}
