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
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParams;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.mpp.feature.energy.scheme.EnergyScheme;
import org.anchoranalysis.mpp.feature.energy.scheme.EnergySchemeWithSharedFeatures;
import org.anchoranalysis.mpp.feature.mark.ListUpdatableMarkSetCollection;
import org.anchoranalysis.mpp.feature.mark.MemoCollection;
import org.anchoranalysis.mpp.feature.mark.MemoList;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.set.UpdateMarkSetException;
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

    @Getter private MarksWithEnergyBreakdown marks;

    /**
     * A cached version of the calculations for each energy component in the associated {@link
     * EnergyScheme}
     */
    private MemoCollection memoMarks;

    private Logger logger;

    public VoxelizedMarksWithEnergy(
            MarksWithEnergyBreakdown marks,
            EnergyStack energyStack,
            SharedFeatureMulti sharedFeatures,
            Logger logger)
            throws NamedFeatureCalculateException {
        this(marks, createMemoCollection(marks, energyStack, sharedFeatures, logger), logger);
    }

    /** Creates a shallow-copy of the marks */
    public VoxelizedMarksWithEnergy shallowCopy() {
        return new VoxelizedMarksWithEnergy(
                marks.shallowCopy(), new MemoCollection(memoMarks), logger);
    }

    /** Creates a deep-copy of the marks */
    public VoxelizedMarksWithEnergy deepCopy() {
        return new VoxelizedMarksWithEnergy(
                marks.deepCopy(), new MemoCollection(memoMarks), logger);
    }

    public int indexOf(Mark mark) {
        return marks.getMarks().indexOf(mark);
    }

    public void clean() {
        memoMarks.clean();
    }

    public EnergySchemeWithSharedFeatures getEnergyScheme() {
        return marks.getEnergyScheme();
    }

    public void add(VoxelizedMarkMemo newPxlMark, EnergyStackWithoutParams stack)
            throws NamedFeatureCalculateException {
        marks.add(memoMarks, newPxlMark, stack);
    }

    public void remove(int index, EnergyStackWithoutParams stack)
            throws NamedFeatureCalculateException {
        VoxelizedMarkMemo memoRmv = getMemoForIndex(index);
        marks.remove(memoMarks, index, memoRmv, stack);
    }

    public void remove(VoxelizedMarkMemo memoToRemove, EnergyStackWithoutParams stack)
            throws NamedFeatureCalculateException {
        marks.remove(memoMarks, memoToRemove, stack);
    }

    public void removeTwo(int index1, int index2, EnergyStackWithoutParams stack)
            throws NamedFeatureCalculateException {
        marks.removeTwo(memoMarks, index1, index2, stack);
    }

    // Does the pairs hash only contains items contained in a particular configuration
    public boolean isMarksSpan() {
        return marks.getPair().isMarksSpan(marks.getMarks());
    }

    // calculates a new energy and configuration based upon a mark at a particular index
    //   changing into new mark
    public void exchange(int index, VoxelizedMarkMemo newMark, EnergyStack energyStack)
            throws NamedFeatureCalculateException {
        marks.exchange(memoMarks, index, newMark, energyStack);
    }

    public double getEnergyTotal() {
        return marks.getEnergyTotal();
    }

    // Adds all current marks to the updatable-pair list
    public void addAllToUpdatablePairList(ListUpdatableMarkSetCollection updatablePairList)
            throws UpdateMarkSetException {
        updatablePairList.add(memoMarks);
    }

    // Adds the particular memo to the updatable pair-list
    public void addToUpdatablePairList(
            ListUpdatableMarkSetCollection updatablePairList, VoxelizedMarkMemo memo)
            throws UpdateMarkSetException {
        updatablePairList.add(memoMarks, memo);
    }

    // Removes a memo from the updatable pair-list
    public void rmvFromUpdatablePairList(
            ListUpdatableMarkSetCollection updatablePairList, Mark mark)
            throws UpdateMarkSetException {
        VoxelizedMarkMemo memo = getMemoForMark(mark);
        updatablePairList.remove(memoMarks, memo);
    }

    // Exchanges one mark with another on the updatable pair list
    public void exchangeOnUpdatablePairList(
            ListUpdatableMarkSetCollection updatablePairList,
            Mark markExst,
            VoxelizedMarkMemo memoNew)
            throws UpdateMarkSetException {
        VoxelizedMarkMemo memoExst = getMemoForMark(markExst);
        updatablePairList.exchange(memoMarks, memoExst, indexOf(markExst), memoNew);
    }

    public MemoList createDuplicatePxlMarkMemoList() {
        MemoList list = new MemoList();
        list.addAll(memoMarks);
        return list;
    }

    public VoxelizedMarkMemo getMemoForMark(Mark mark) {
        return memoMarks.getMemoForMark(marks.getMarks(), mark);
    }

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

    // The initial calculation of the Energy, thereafter it can be updated
    private static MemoCollection createMemoCollection(
            MarksWithEnergyBreakdown marks,
            EnergyStack energyStack,
            SharedFeatureMulti sharedFeatures,
            Logger logger)
            throws NamedFeatureCalculateException {
        try {
            marks.init();

            MemoCollection memo =
                    new MemoCollection(
                            marks.getIndividual(),
                            energyStack.withoutParams(),
                            marks.getMarks(),
                            marks.getEnergyScheme());

            marks.getPair().initUpdatableMarkSet(memo, energyStack, logger, sharedFeatures);

            // Some energy components need to be calculated in terms of interactions
            //  this we need to track in an intelligent way
            marks.updateTotal(memo, energyStack.withoutParams());

            return memo;
        } catch (InitException e) {
            throw new NamedFeatureCalculateException(e);
        }
    }

    public final int size() {
        return marks.size();
    }
}
