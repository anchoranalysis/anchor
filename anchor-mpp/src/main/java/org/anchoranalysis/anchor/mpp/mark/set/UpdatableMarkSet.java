/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark.set;

import org.anchoranalysis.anchor.mpp.pxlmark.memo.MemoForIndex;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

public interface UpdatableMarkSet {

    void initUpdatableMarkSet(
            MemoForIndex marks,
            NRGStackWithParams nrgStack,
            Logger logger,
            SharedFeatureMulti sharedFeatures)
            throws InitException;

    void add(MemoForIndex marksExisting, VoxelizedMarkMemo newMark) throws UpdateMarkSetException;

    void exchange(
            MemoForIndex pxlMarkMemoList,
            VoxelizedMarkMemo oldMark,
            int indexOldMark,
            VoxelizedMarkMemo newMark)
            throws UpdateMarkSetException;

    void rmv(MemoForIndex marksExisting, VoxelizedMarkMemo mark) throws UpdateMarkSetException;
}
