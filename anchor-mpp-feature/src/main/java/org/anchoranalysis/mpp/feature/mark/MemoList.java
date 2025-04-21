/*-
 * #%L
 * anchor-mpp-feature
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

package org.anchoranalysis.mpp.feature.mark;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.mpp.mark.voxelized.memo.MemoForIndex;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;

/**
 * A list of {@link VoxelizedMarkMemo} objects that implements {@link MemoForIndex} and {@link
 * Iterable}.
 *
 * <p>This class provides methods to manipulate and access the list of voxelized mark memos.
 */
@EqualsAndHashCode(callSuper = false)
public class MemoList implements MemoForIndex, Iterable<VoxelizedMarkMemo> {

    /** The underlying list storing the {@link VoxelizedMarkMemo} objects. */
    private List<VoxelizedMarkMemo> delegate = new ArrayList<>();

    /**
     * Adds all elements from another {@link MemoForIndex} to this list.
     *
     * @param src the source {@link MemoForIndex} to add elements from
     */
    public void addAll(MemoForIndex src) {
        for (int i = 0; i < src.size(); i++) {
            add(src.getMemoForIndex(i));
        }
    }

    @Override
    public VoxelizedMarkMemo getMemoForIndex(int index) {
        return get(index);
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    public int size() {
        return delegate.size();
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public VoxelizedMarkMemo get(int index) {
        return delegate.get(index);
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return {@code true} (as specified by {@link Collection#add})
     */
    public boolean add(VoxelizedMarkMemo e) {
        return delegate.add(e);
    }

    /**
     * Removes the element at the specified position in this list.
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public VoxelizedMarkMemo remove(int index) {
        return delegate.remove(index);
    }

    /**
     * Removes the first occurrence of the specified element from this list, if it is present.
     *
     * @param memo element to be removed from this list, if present
     * @return {@code true} if this list contained the specified element
     */
    public boolean remove(VoxelizedMarkMemo memo) {
        return delegate.remove(memo);
    }

    @Override
    public Iterator<VoxelizedMarkMemo> iterator() {
        return delegate.iterator();
    }

    /**
     * Replaces the element at the specified position in this list with the specified element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public VoxelizedMarkMemo set(int index, VoxelizedMarkMemo element) {
        return delegate.set(index, element);
    }
}
