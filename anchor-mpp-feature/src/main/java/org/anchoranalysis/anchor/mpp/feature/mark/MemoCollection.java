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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.anchor.mpp.feature.energy.saved.EnergySavedInd;
import org.anchoranalysis.anchor.mpp.feature.energy.scheme.EnergySchemeWithSharedFeatures;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParams;
import org.anchoranalysis.feature.energy.EnergyTotal;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.mark.voxelized.memo.MemoForIndex;
import org.anchoranalysis.mpp.mark.voxelized.memo.PxlMarkMemoFactory;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;

/**
 * A collection of memoized marks on which energies can be derived.
 *
 * <p>TODO, can it be united with {@link MemoList}?
 *
 * @author Owen Feehan
 */
public class MemoCollection implements Serializable, MemoForIndex {

    /** */
    private static final long serialVersionUID = 9067220044867268357L;

    // We keep the pixelized version of the marks
    private transient List<VoxelizedMarkMemo> pxlMarkMemo;

    private transient RegionMap regionMap;

    // START CONSTRUCTORS
    public MemoCollection() {
        pxlMarkMemo = new ArrayList<>();
    }

    public MemoCollection(
            EnergySavedInd savedInd,
            EnergyStackWithoutParams energyStack,
            MarkCollection marks,
            EnergySchemeWithSharedFeatures energySchemeTotal)
            throws NamedFeatureCalculateException {
        this.regionMap = energySchemeTotal.getRegionMap();
        calculateFreshInd(savedInd, energyStack, marks, energySchemeTotal);
    }

    public MemoCollection(MemoCollection src) {
        this.pxlMarkMemo = new ArrayList<>();
        this.regionMap = src.getRegionMap();
        for (VoxelizedMarkMemo pmm : src.pxlMarkMemo) {
            this.pxlMarkMemo.add(pmm);
        }
    }
    // END CONSTRUCTORS

    public void clean() {
        pxlMarkMemo = null;
    }

    @Override
    public int size() {
        return pxlMarkMemo.size();
    }

    public RegionMap getRegionMap() {
        return regionMap;
    }

    public VoxelizedMarkMemo getMemoForMark(MarkCollection marks, Mark mark) {
        int index = marks.indexOf(mark);
        if (index == -1) {
            throw new AnchorFriendlyRuntimeException("Mark doesn't exist in marks");
        }
        return pxlMarkMemo.get(index);
    }

    public VoxelizedMarkMemo getMemoForIndex(int index) {
        return pxlMarkMemo.get(index);
    }

    public int getIndexForMemo(VoxelizedMarkMemo memo) {
        for (int i = 0; i < pxlMarkMemo.size(); i++) {

            VoxelizedMarkMemo pmm = pxlMarkMemo.get(i);

            if (pmm.equals(memo)) {
                return i;
            }
        }
        return -1;
    }

    // calculate fresh ind
    private void calculateFreshInd(
            EnergySavedInd energySavedInd,
            EnergyStackWithoutParams energyStack,
            MarkCollection marks,
            EnergySchemeWithSharedFeatures energySchemeTotal)
            throws NamedFeatureCalculateException {

        energySavedInd.setEnergyTotal(0);

        this.pxlMarkMemo = new ArrayList<>();

        energySavedInd.resetInd();

        // Some energy components need to be calculated individually
        for (Mark mrk : marks) {

            VoxelizedMarkMemo pmm =
                    PxlMarkMemoFactory.create(mrk, energyStack, energySchemeTotal.getRegionMap());
            this.pxlMarkMemo.add(pmm);

            EnergyTotal ind = energySchemeTotal.totalIndividual(pmm, energyStack);
            energySavedInd.add(ind);
        }
    }

    // calculates a new energy and configuration based upon a mark at a particular index
    //   changing into new mark
    public VoxelizedMarkMemo exchange(
            EnergySavedInd energySavedInd,
            int index,
            VoxelizedMarkMemo newMark,
            EnergyStackWithoutParams stack,
            EnergySchemeWithSharedFeatures energySchemeTotal)
            throws NamedFeatureCalculateException {
        // We calculate energy for individual components
        EnergyTotal ind = energySchemeTotal.totalIndividual(newMark, stack);
        energySavedInd.exchange(index, ind);

        this.pxlMarkMemo.set(index, newMark);

        return newMark;
    }

    public VoxelizedMarkMemo add(
            EnergySavedInd energySavedInd,
            VoxelizedMarkMemo memo,
            EnergyStackWithoutParams stack,
            EnergySchemeWithSharedFeatures energyScheme)
            throws NamedFeatureCalculateException {
        EnergyTotal energy = energyScheme.totalIndividual(memo, stack);

        // We calculate energy for individual components
        this.pxlMarkMemo.add(memo);

        energySavedInd.add(energy);

        return memo;
    }

    public void remove(EnergySavedInd energySavedInd, int index) {

        energySavedInd.rmv(index);

        this.pxlMarkMemo.remove(index);
    }

    public void removeTwo(EnergySavedInd energySavedInd, int index1, int index2) {
        int indexMax = Math.max(index1, index2);
        int indexMin = Math.min(index1, index2);

        remove(energySavedInd, indexMax);
        remove(energySavedInd, indexMin);
    }

    public void assertValid() {
        for (VoxelizedMarkMemo pmm : pxlMarkMemo) {
            if (pmm == null) {
                assert false;
            }
        }
    }

    public MarkCollection asMarks() {
        MarkCollection marks = new MarkCollection();
        for (int i = 0; i < this.size(); i++) {
            VoxelizedMarkMemo pmm = this.getMemoForIndex(i);
            marks.add(pmm.getMark());
        }
        return marks;
    }
}
