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

package org.anchoranalysis.anchor.mpp.feature.mark;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.mpp.mark.voxelized.memo.MemoForIndex;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class MemoList implements MemoForIndex {

    private List<VoxelizedMarkMemo> delegate = new ArrayList<>();

    public void addAll(MemoForIndex src) {

        for (int i = 0; i < src.size(); i++) {
            add(src.getMemoForIndex(i));
        }
    }

    @Override
    public VoxelizedMarkMemo getMemoForIndex(int index) {
        return get(index);
    }

    public int size() {
        return delegate.size();
    }

    public VoxelizedMarkMemo get(int index) {
        return delegate.get(index);
    }

    public boolean add(VoxelizedMarkMemo e) {
        return delegate.add(e);
    }

    public VoxelizedMarkMemo remove(int index) {
        return delegate.remove(index);
    }

    public boolean remove(VoxelizedMarkMemo o) {
        return delegate.remove(o);
    }
}
