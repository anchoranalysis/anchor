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

package org.anchoranalysis.mpp.feature.energy.marks;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParameters;
import org.anchoranalysis.feature.shared.SharedFeatures;
import org.anchoranalysis.mpp.feature.energy.scheme.EnergyScheme;
import org.anchoranalysis.mpp.feature.energy.scheme.EnergySchemeWithSharedFeatures;
import org.anchoranalysis.mpp.feature.mark.EnergyMemoList;
import org.anchoranalysis.mpp.feature.mark.MemoList;
import org.anchoranalysis.mpp.feature.mark.UpdatableMarksList;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.UpdateMarkSetException;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;

/**
 * A collection of marks, their voxelized equivalents, and associated energy.
 *
 * <p>i.e. A {@link MarksWithEnergyBreakdown} together with the cached voxelization calculations.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class VoxelizedMarksWithEnergy {

    /** The marks with their energy breakdown. */
    @Getter private MarksWithEnergyBreakdown marks;

    /**
     * A cached version of the calculations for each energy component in the associated {@link
     * EnergyScheme}.
     */
    private EnergyMemoList memoMarks;

    /** Logger for recording messages. */
    private Logger logger;

    /**
     * Creates a new instance with the given marks, energy stack, shared features, and logger.
     *
     * @param marks the marks with their energy breakdown
     * @param energyStack the energy stack
     * @param sharedFeatures shared features for calculations
     * @param logger the logger for recording messages
     * @throws NamedFeatureCalculateException if there's an error calculating named features
     */
    public VoxelizedMarksWithEnergy(
            MarksWithEnergyBreakdown marks,
            EnergyStack energyStack,
            SharedFeatures sharedFeatures,
            Logger logger)
            throws NamedFeatureCalculateException {
        this(marks, createMemoCollection(marks, energyStack, sharedFeatures, logger), logger);
    }

    /**
     * Creates a shallow-copy of the marks.
     *
     * @return a new {@link VoxelizedMarksWithEnergy} instance with shallow-copied marks
     */
    public VoxelizedMarksWithEnergy shallowCopy() {
        return new VoxelizedMarksWithEnergy(
                marks.shallowCopy(), new EnergyMemoList(memoMarks), logger);
    }

    /**
     * Creates a deep-copy of the marks.
     *
     * @return a new {@link VoxelizedMarksWithEnergy} instance with deep-copied marks
     */
    public VoxelizedMarksWithEnergy deepCopy() {
        return new VoxelizedMarksWithEnergy(
                marks.deepCopy(), new EnergyMemoList(memoMarks), logger);
    }

    /**
     * Gets the index of a specific mark.
     *
     * @param mark the mark to find
     * @return the index of the mark, or -1 if not found
     */
    public int indexOf(Mark mark) {
        return marks.getMarks().indexOf(mark);
    }

    /**
     * Cleans up resources associated with the voxelized marks.
     */
    public void clean() {
        memoMarks.clean();
    }

    /**
     * Gets the energy scheme with shared features.
     *
     * @return the {@link EnergySchemeWithSharedFeatures}
     */
    public EnergySchemeWithSharedFeatures getEnergyScheme() {
        return marks.getEnergyScheme();
    }

    /**
     * Adds a new voxelized mark to the collection.
     *
     * @param newPxlMark the new voxelized mark to add
     * @param stack the energy stack without parameters
     * @throws NamedFeatureCalculateException if there's an error calculating named features
     */
    public void add(VoxelizedMarkMemo newPxlMark, EnergyStackWithoutParameters stack)
            throws NamedFeatureCalculateException {
        marks.add(memoMarks, newPxlMark, stack);
    }

    /**
     * Removes a mark at the specified index.
     *
     * @param index the index of the mark to remove
     * @param stack the energy stack without parameters
     * @throws NamedFeatureCalculateException if there's an error calculating named features
     */
    public void remove(int index, EnergyStackWithoutParameters stack)
            throws NamedFeatureCalculateException {
        VoxelizedMarkMemo memoRmv = getMemoForIndex(index);
        marks.remove(memoMarks, index, memoRmv, stack);
    }

    /**
     * Removes a specific voxelized mark.
     *
     * @param memoToRemove the voxelized mark to remove
     * @param stack the energy stack without parameters
     * @throws NamedFeatureCalculateException if there's an error calculating named features
     */
    public void remove(VoxelizedMarkMemo memoToRemove, EnergyStackWithoutParameters stack)
            throws NamedFeatureCalculateException {
        marks.remove(memoMarks, memoToRemove, stack);
    }

    /**
     * Removes two marks at the specified indices.
     *
     * @param index1 the index of the first mark to remove
     * @param index2 the index of the second mark to remove
     * @param stack the energy stack without parameters
     * @throws NamedFeatureCalculateException if there's an error calculating named features
     */
    public void removeTwo(int index1, int index2, EnergyStackWithoutParameters stack)
            throws NamedFeatureCalculateException {
        marks.removeTwo(memoMarks, index1, index2, stack);
    }

    /**
     * Checks if the pairs hash only contains items contained in the current configuration.
     *
     * @return true if the pairs hash spans only the current marks, false otherwise
     */
    public boolean isMarksSpan() {
        return marks.getPair().isMarksSpan(marks.getMarks());
    }

    /**
     * Exchanges a mark at a particular index with a new mark and recalculates energy.
     *
     * @param index the index of the mark to exchange
     * @param newMark the new voxelized mark
     * @param energyStack the energy stack
     * @throws NamedFeatureCalculateException if there's an error calculating named features
     */
    public void exchange(int index, VoxelizedMarkMemo newMark, EnergyStack energyStack)
            throws NamedFeatureCalculateException {
        marks.exchange(memoMarks, index, newMark, energyStack);
    }

    /**
     * Gets the total energy of all marks.
     *
     * @return the total energy as a double
     */
    public double getEnergyTotal() {
        return marks.getEnergyTotal();
    }

    /**
     * Adds all current marks to the updatable pair list.
     *
     * @param updatablePairList the list to add marks to
     * @throws UpdateMarkSetException if there's an error updating the mark set
     */
    public void addAllToUpdatablePairList(UpdatableMarksList updatablePairList)
            throws UpdateMarkSetException {
        updatablePairList.add(memoMarks);
    }

    /**
     * Adds a particular voxelized mark to the updatable pair list.
     *
     * @param updatablePairList the list to add the mark to
     * @param memo the voxelized mark to add
     * @throws UpdateMarkSetException if there's an error updating the mark set
     */
    public void addToUpdatablePairList(UpdatableMarksList updatablePairList, VoxelizedMarkMemo memo)
            throws UpdateMarkSetException {
        updatablePairList.add(memoMarks, memo);
    }

    /**
     * Removes a mark from the updatable pair list.
     *
     * @param updatablePairList the list to remove the mark from
     * @param mark the mark to remove
     * @throws UpdateMarkSetException if there's an error updating the mark set
     */
    public void rmvFromUpdatablePairList(UpdatableMarksList updatablePairList, Mark mark)
            throws UpdateMarkSetException {
        VoxelizedMarkMemo memo = getMemoForMark(mark);
        updatablePairList.remove(memoMarks, memo);
    }

    /**
     * Exchanges one mark with another on the updatable pair list.
     *
     * @param updatablePairList the list to perform the exchange on
     * @param markExst the existing mark to be replaced
     * @param memoNew the new voxelized mark
     * @throws UpdateMarkSetException if there's an error updating the mark set
     */
    public void exchangeOnUpdatablePairList(
            UpdatableMarksList updatablePairList, Mark markExst, VoxelizedMarkMemo memoNew)
            throws UpdateMarkSetException {
        VoxelizedMarkMemo memoExst = getMemoForMark(markExst);
        updatablePairList.exchange(memoMarks, memoExst, indexOf(markExst), memoNew);
    }

    /**
     * Creates a duplicate of the voxelized mark memo list.
     *
     * @return a new {@link MemoList} containing duplicates of all voxelized marks
     */
    public MemoList createDuplicatePxlMarkMemoList() {
        MemoList list = new MemoList();
        list.addAll(memoMarks);
        return list;
    }

    /**
     * Gets the voxelized memo for a specific mark.
     *
     * @param mark the mark to get the memo for
     * @return the {@link VoxelizedMarkMemo} for the given mark
     */
    public VoxelizedMarkMemo getMemoForMark(Mark mark) {
        return memoMarks.getMemoForMark(marks.getMarks(), mark);
    }

    /**
     * Gets the voxelized memo for a specific index.
     *
     * @param index the index of the memo to get
     * @return the {@link VoxelizedMarkMemo} at the given index
     */
    public VoxelizedMarkMemo getMemoForIndex(int index) {
        return memoMarks.getMemoForIndex(index);
    }

    @Override
    public String toString() {

        String newLine = System.getProperty("line.separator");

        StringBuilder s = new StringBuilder("{");

        s.append(
                String.format(
                        "size=%d, total=%e, ind=%e, pair=%e%n",
                        marks.size(),
                        marks.getEnergyTotal(),
                        marks.getIndividual().getEnergyTotal(),
                        marks.getPair().getEnergyTotal()));

        s.append(marks.getIndividual().describeMarks(marks.getMarks()));
        s.append(marks.getPair().toString());

        s.append("}");
        s.append(newLine);

        return s.toString();
    }

    /**
     * Gets the number of marks in the collection.
     *
     * @return the number of marks
     */
    public final int size() {
        return marks.size();
    }

    // The initial calculation of the Energy, thereafter it can be updated
    private static EnergyMemoList createMemoCollection(
            MarksWithEnergyBreakdown marks,
            EnergyStack energyStack,
            SharedFeatures sharedFeatures,
            Logger logger)
            throws NamedFeatureCalculateException {
        try {
            marks.initialize();

            EnergyMemoList memo =
                    new EnergyMemoList(
                            marks.getIndividual(),
                            energyStack.withoutParameters(),
                            marks.getMarks(),
                            marks.getEnergyScheme());

            marks.getPair().initUpdatableMarks(memo, energyStack, logger, sharedFeatures);

            // Some energy components need to be calculated in terms of interactions
            //  this we need to track in an intelligent way
            marks.updateTotal(memo, energyStack.withoutParameters());

            return memo;
        } catch (InitializeException e) {
            throw new NamedFeatureCalculateException(e);
        }
    }
}