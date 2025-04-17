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

import java.io.Serializable;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParameters;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.feature.energy.EnergyTotal;
import org.anchoranalysis.mpp.feature.energy.saved.EnergySavedIndividual;
import org.anchoranalysis.mpp.feature.energy.scheme.EnergySchemeWithSharedFeatures;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.mark.voxelized.memo.MemoForIndex;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemoFactory;

/**
 * A collection of memoized marks on which energies can be derived.
 *
 * <p>This class implements {@link Serializable} for persistence and {@link MemoForIndex} for
 * indexing operations.
 *
 * @author Owen Feehan
 */
public class EnergyMemoList implements Serializable, MemoForIndex {

    private static final long serialVersionUID = 9067220044867268357L;

    /** We keep the pixelized version of the marks. */
    private transient MemoList list;

    /** The region map associated with the energy memo list. */
    private transient RegionMap regionMap;

    /** Creates an empty energy memo list. */
    public EnergyMemoList() {
        list = new MemoList();
    }

    /**
     * Creates an energy memo list with the given parameters.
     *
     * @param savedInd the saved individual energies
     * @param energyStack the energy stack without parameters
     * @param marks the collection of marks
     * @param energySchemeTotal the energy scheme with shared features
     * @throws NamedFeatureCalculateException if there's an error calculating named features
     */
    public EnergyMemoList(
            EnergySavedIndividual savedInd,
            EnergyStackWithoutParameters energyStack,
            MarkCollection marks,
            EnergySchemeWithSharedFeatures energySchemeTotal)
            throws NamedFeatureCalculateException {
        this.regionMap = energySchemeTotal.getRegionMap();
        calculateFreshInd(savedInd, energyStack, marks, energySchemeTotal);
    }

    /**
     * Creates a copy of an existing energy memo list.
     *
     * @param source the source energy memo list to copy
     */
    public EnergyMemoList(EnergyMemoList source) {
        this.list = new MemoList();
        this.regionMap = source.getRegionMap();
        for (VoxelizedMarkMemo pmm : source.list) {
            this.list.add(pmm);
        }
    }

    /** Cleans up the energy memo list by setting the list to null. */
    public void clean() {
        list = null;
    }

    @Override
    public int size() {
        return list.size();
    }

    /**
     * Gets the region map associated with this energy memo list.
     *
     * @return the {@link RegionMap}
     */
    public RegionMap getRegionMap() {
        return regionMap;
    }

    /**
     * Gets the memo for a specific mark in the collection.
     *
     * @param marks the collection of marks
     * @param mark the specific mark to find
     * @return the {@link VoxelizedMarkMemo} for the given mark
     * @throws AnchorFriendlyRuntimeException if the mark doesn't exist in the collection
     */
    public VoxelizedMarkMemo getMemoForMark(MarkCollection marks, Mark mark) {
        int index = marks.indexOf(mark);
        if (index == -1) {
            throw new AnchorFriendlyRuntimeException("Mark doesn't exist in marks");
        }
        return list.get(index);
    }

    /**
     * Gets the memo for a specific index.
     *
     * @param index the index of the memo
     * @return the {@link VoxelizedMarkMemo} at the given index
     */
    public VoxelizedMarkMemo getMemoForIndex(int index) {
        return list.get(index);
    }

    /**
     * Gets the index for a specific memo.
     *
     * @param memo the memo to find
     * @return the index of the memo, or -1 if not found
     */
    public int getIndexForMemo(VoxelizedMarkMemo memo) {
        for (int i = 0; i < list.size(); i++) {

            VoxelizedMarkMemo pmm = list.get(i);

            if (pmm.equals(memo)) {
                return i;
            }
        }
        return -1;
    }

    // calculate fresh ind
    private void calculateFreshInd(
            EnergySavedIndividual energySavedInd,
            EnergyStackWithoutParameters energyStack,
            MarkCollection marks,
            EnergySchemeWithSharedFeatures energySchemeTotal)
            throws NamedFeatureCalculateException {

        energySavedInd.setEnergyTotal(0);

        this.list = new MemoList();

        energySavedInd.resetInd();

        // Some energy components need to be calculated individually
        for (Mark mrk : marks) {

            VoxelizedMarkMemo pmm =
                    VoxelizedMarkMemoFactory.create(
                            mrk, energyStack, energySchemeTotal.getRegionMap());
            this.list.add(pmm);

            EnergyTotal ind = energySchemeTotal.totalIndividual(pmm, energyStack);
            energySavedInd.add(ind);
        }
    }

    /**
     * Exchanges a mark at a specific index with a new mark and recalculates energies.
     *
     * @param energySavedInd the saved individual energies
     * @param index the index of the mark to exchange
     * @param newMark the new mark to insert
     * @param stack the energy stack without parameters
     * @param energySchemeTotal the energy scheme with shared features
     * @return the {@link VoxelizedMarkMemo} for the new mark
     * @throws NamedFeatureCalculateException if there's an error calculating named features
     */
    public VoxelizedMarkMemo exchange(
            EnergySavedIndividual energySavedInd,
            int index,
            VoxelizedMarkMemo newMark,
            EnergyStackWithoutParameters stack,
            EnergySchemeWithSharedFeatures energySchemeTotal)
            throws NamedFeatureCalculateException {
        // We calculate energy for individual components
        EnergyTotal ind = energySchemeTotal.totalIndividual(newMark, stack);
        energySavedInd.exchange(index, ind);

        this.list.set(index, newMark);

        return newMark;
    }

    /**
     * Adds a new mark to the list and calculates its energy.
     *
     * @param energySavedInd the saved individual energies
     * @param memo the memo for the new mark
     * @param stack the energy stack without parameters
     * @param energyScheme the energy scheme with shared features
     * @return the {@link VoxelizedMarkMemo} for the added mark
     * @throws NamedFeatureCalculateException if there's an error calculating named features
     */
    public VoxelizedMarkMemo add(
            EnergySavedIndividual energySavedInd,
            VoxelizedMarkMemo memo,
            EnergyStackWithoutParameters stack,
            EnergySchemeWithSharedFeatures energyScheme)
            throws NamedFeatureCalculateException {
        EnergyTotal energy = energyScheme.totalIndividual(memo, stack);

        // We calculate energy for individual components
        this.list.add(memo);

        energySavedInd.add(energy);

        return memo;
    }

    /**
     * Removes a mark at a specific index and updates energies.
     *
     * @param energySavedInd the saved individual energies
     * @param index the index of the mark to remove
     */
    public void remove(EnergySavedIndividual energySavedInd, int index) {

        energySavedInd.rmv(index);

        this.list.remove(index);
    }

    /**
     * Removes two marks at specific indices and updates energies.
     *
     * @param energySavedInd the saved individual energies
     * @param index1 the index of the first mark to remove
     * @param index2 the index of the second mark to remove
     */
    public void removeTwo(EnergySavedIndividual energySavedInd, int index1, int index2) {
        int indexMax = Math.max(index1, index2);
        int indexMin = Math.min(index1, index2);

        remove(energySavedInd, indexMax);
        remove(energySavedInd, indexMin);
    }

    /**
     * Converts the energy memo list to a collection of marks.
     *
     * @return a {@link MarkCollection} containing all the marks in this list
     */
    public MarkCollection asMarks() {
        MarkCollection marks = new MarkCollection();
        for (int i = 0; i < this.size(); i++) {
            VoxelizedMarkMemo pmm = this.getMemoForIndex(i);
            marks.add(pmm.getMark());
        }
        return marks;
    }
}
