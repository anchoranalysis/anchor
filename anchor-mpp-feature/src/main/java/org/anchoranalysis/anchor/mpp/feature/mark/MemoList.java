/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.mark;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.MemoForIndex;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;

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
