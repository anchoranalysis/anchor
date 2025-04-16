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

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParameters;
import org.anchoranalysis.mpp.feature.energy.saved.EnergySavedAll;
import org.anchoranalysis.mpp.feature.energy.saved.EnergySavedIndividual;
import org.anchoranalysis.mpp.feature.energy.saved.EnergySavedPairs;
import org.anchoranalysis.mpp.feature.energy.scheme.EnergySchemeWithSharedFeatures;
import org.anchoranalysis.mpp.feature.mark.EnergyMemoList;
import org.anchoranalysis.mpp.feature.mark.MemoList;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.mark.UpdateMarkSetException;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;

/**
 * Marks with both the total energy and a breakdown by clique.
 *
 * <p>This class provides functionality to manage and calculate energies for a collection of marks,
 * including individual, pair, and all-mark energies.
 */
@AllArgsConstructor
public class MarksWithEnergyBreakdown implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The marks with their total energy. */
    private final MarksWithTotalEnergy marks;

    /** Energy saved for individual marks. */
    @Getter private EnergySavedIndividual individual;

    /** Every combination of interactions between marks and the associated energy. */
    @Getter private transient EnergySavedPairs pair;

    /**
     * Certain features are stored for every object, so that we can reference them in our
     * calculations for the 'all' component.
     */
    @Getter @Setter private transient EnergySavedAll all;

    /**
     * Creates a new instance with the given marks and total energy.
     *
     * @param marks the {@link MarksWithTotalEnergy} containing marks with their total energy
     */
    public MarksWithEnergyBreakdown(MarksWithTotalEnergy marks) {
        this.marks = marks;
    }

    /**
     * Initializes the energy calculations.
     *
     * @throws NamedFeatureCalculateException if there's an error during initialization
     */
    public void initialize() throws NamedFeatureCalculateException {
        this.individual = new EnergySavedIndividual();
        try {
            this.pair = new EnergySavedPairs(marks.getEnergyScheme().createAddCriteria());
            this.all = new EnergySavedAll();
        } catch (CreateException e) {
            throw new NamedFeatureCalculateException(e);
        }
    }

    /** Asserts that the energy calculations are valid. */
    public void assertValid() {
        if (this.individual != null) {
            this.individual.assertValid();
        }
        if (this.pair != null) {
            this.pair.assertValid();
        }
        if (this.individual != null && this.pair != null) {
            assert (Math.abs(
                            marks.getEnergyTotal()
                                    - individual.getEnergyTotal()
                                    - pair.getEnergyTotal())
                    < 1e-3);
        }
    }

    /**
     * Gets the marks with their total energy.
     *
     * @return the {@link MarksWithTotalEnergy} containing marks with total energy
     */
    public MarksWithTotalEnergy getMarksWithTotalEnergy() {
        return marks;
    }

    /**
     * Updates the total energy based on the current state.
     *
     * @param pxlMarkMemoList the {@link EnergyMemoList} containing energy memos
     * @param stack the {@link EnergyStackWithoutParameters} for energy calculations
     * @throws NamedFeatureCalculateException if there's an error during calculation
     */
    public void updateTotal(EnergyMemoList pxlMarkMemoList, EnergyStackWithoutParameters stack)
            throws NamedFeatureCalculateException {

        // We calculate an all value
        this.all.calc(pxlMarkMemoList, marks.getEnergyScheme(), stack);

        double total =
                this.individual.getEnergyTotal()
                        + this.pair.getEnergyTotal()
                        + this.all.getEnergyTotal();
        assert (!Double.isNaN(total));
        marks.setEnergyTotal(total);
    }

    /**
     * Creates a shallow copy of this instance.
     *
     * @return a new {@link MarksWithEnergyBreakdown} instance with shallow copies of the internal
     *     state
     */
    public MarksWithEnergyBreakdown shallowCopy() {
        return new MarksWithEnergyBreakdown(
                marks.shallowCopy(),
                individual.shallowCopy(),
                pair.shallowCopy(),
                all.shallowCopy());
    }

    /**
     * Creates a deep copy of this instance.
     *
     * @return a new {@link MarksWithEnergyBreakdown} instance with deep copies of the internal
     *     state
     */
    public MarksWithEnergyBreakdown deepCopy() {
        return new MarksWithEnergyBreakdown(
                marks.deepCopy(), individual.deepCopy(), pair.deepCopy(), all.deepCopy());
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
     * Gets the collection of marks.
     *
     * @return the {@link MarkCollection} containing all marks
     */
    public MarkCollection getMarks() {
        return marks.getMarks();
    }

    /**
     * Gets the energy scheme with shared features.
     *
     * @return the {@link EnergySchemeWithSharedFeatures} used for energy calculations
     */
    public EnergySchemeWithSharedFeatures getEnergyScheme() {
        return marks.getEnergyScheme();
    }

    /**
     * Adds a new mark to the collection and updates energies.
     *
     * @param wrapperInd the {@link EnergyMemoList} containing energy memos
     * @param newPxlMarkMemo the new {@link VoxelizedMarkMemo} to add
     * @param stack the {@link EnergyStackWithoutParameters} for energy calculations
     * @throws NamedFeatureCalculateException if there's an error during calculation
     */
    public void add(
            EnergyMemoList wrapperInd,
            VoxelizedMarkMemo newPxlMarkMemo,
            EnergyStackWithoutParameters stack)
            throws NamedFeatureCalculateException {

        marks.add(newPxlMarkMemo);

        // Individuals
        wrapperInd.add(getIndividual(), newPxlMarkMemo, stack, marks.getEnergyScheme());

        // Pairs
        try {
            getPair().add(wrapperInd, newPxlMarkMemo);
        } catch (UpdateMarkSetException e) {
            throw new NamedFeatureCalculateException(e);
        }

        updateTotal(wrapperInd, stack);
    }

    /**
     * Removes a specific mark from the collection and updates energies.
     *
     * @param wrapperInd the {@link EnergyMemoList} containing energy memos
     * @param markToRemove the {@link VoxelizedMarkMemo} to remove
     * @param stack the {@link EnergyStackWithoutParameters} for energy calculations
     * @throws NamedFeatureCalculateException if there's an error during calculation
     */
    public void remove(
            EnergyMemoList wrapperInd,
            VoxelizedMarkMemo markToRemove,
            EnergyStackWithoutParameters stack)
            throws NamedFeatureCalculateException {

        int index = wrapperInd.getIndexForMemo(markToRemove);
        assert (index != -1);
        remove(wrapperInd, index, markToRemove, stack);
    }

    /**
     * Removes a mark at a specific index from the collection and updates energies.
     *
     * @param wrapperInd the {@link EnergyMemoList} containing energy memos
     * @param index the index of the mark to remove
     * @param markToRemove the {@link VoxelizedMarkMemo} to remove
     * @param stack the {@link EnergyStackWithoutParameters} for energy calculations
     * @throws NamedFeatureCalculateException if there's an error during calculation
     */
    public void remove(
            EnergyMemoList wrapperInd,
            int index,
            VoxelizedMarkMemo markToRemove,
            EnergyStackWithoutParameters stack)
            throws NamedFeatureCalculateException {
        try {
            getPair().remove(wrapperInd, markToRemove);
            wrapperInd.remove(getIndividual(), index);

            remove(index);

            updateTotal(wrapperInd, stack);
        } catch (UpdateMarkSetException e) {
            throw new NamedFeatureCalculateException(e);
        }
    }

    /**
     * Removes two marks at specific indices from the collection and updates energies.
     *
     * @param wrapperInd the {@link EnergyMemoList} containing energy memos
     * @param index1 the index of the first mark to remove
     * @param index2 the index of the second mark to remove
     * @param energyStack the {@link EnergyStackWithoutParameters} for energy calculations
     * @throws NamedFeatureCalculateException if there's an error during calculation
     */
    public void removeTwo(
            EnergyMemoList wrapperInd,
            int index1,
            int index2,
            EnergyStackWithoutParameters energyStack)
            throws NamedFeatureCalculateException {

        VoxelizedMarkMemo memoRmv1 = wrapperInd.getMemoForIndex(index1);
        VoxelizedMarkMemo memoRmv2 = wrapperInd.getMemoForIndex(index2);

        wrapperInd.removeTwo(getIndividual(), index1, index2);

        MarkCollection newMarks = marks.getMarks().shallowCopy();

        MemoList memoList = new MemoList();
        memoList.addAll(wrapperInd);

        try {
            getPair().remove(memoList, memoRmv1);
            memoList.remove(memoRmv1);

            getPair().remove(memoList, memoRmv2);
        } catch (UpdateMarkSetException e) {
            throw new NamedFeatureCalculateException(e);
        }

        newMarks.removeTwo(index1, index2);

        marks.setMarks(newMarks);

        updateTotal(wrapperInd, energyStack);
    }

    /**
     * Exchanges a mark at a specific index with a new mark and updates energies.
     *
     * @param wrapperInd the {@link EnergyMemoList} containing energy memos
     * @param index the index of the mark to exchange
     * @param newMark the new {@link VoxelizedMarkMemo} to replace the existing mark
     * @param energyStack the {@link EnergyStack} for energy calculations
     * @throws NamedFeatureCalculateException if there's an error during calculation
     */
    public void exchange(
            EnergyMemoList wrapperInd,
            int index,
            VoxelizedMarkMemo newMark,
            EnergyStack energyStack)
            throws NamedFeatureCalculateException {

        Mark oldMark = marks.get(index);

        marks.exchange(index, newMark);

        // We do the exchange on the individual first, as our pair is expressed relative
        VoxelizedMarkMemo newPxlMarkMemo =
                wrapperInd.exchange(
                        getIndividual(),
                        index,
                        newMark,
                        energyStack.withoutParameters(),
                        marks.getEnergyScheme());
        try {
            VoxelizedMarkMemo oldPxlMarkMemo = wrapperInd.getMemoForMark(getMarks(), oldMark);
            getPair().exchange(wrapperInd, oldPxlMarkMemo, index, newPxlMarkMemo);
        } catch (UpdateMarkSetException e) {
            throw new NamedFeatureCalculateException(e);
        }

        assert (getPair().isMarksSpan(marks.getMarks()));

        // we can also calculate both afresh, but slower
        updateTotal(wrapperInd, energyStack.withoutParameters());

        assert ((marks.getEnergyTotal()
                        - getIndividual().getEnergyTotal()
                        - getPair().getEnergyTotal())
                < 1e-6);
    }

    /**
     * Removes a mark at a specific index from the collection.
     *
     * @param index the index of the mark to remove
     */
    public void remove(int index) {
        marks.remove(index);
    }

    /**
     * Exchanges a mark at a specific index with a new mark.
     *
     * @param index the index of the mark to exchange
     * @param newMark the new {@link VoxelizedMarkMemo} to replace the existing mark
     */
    public void exchange(int index, VoxelizedMarkMemo newMark) {
        marks.exchange(index, newMark);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {

        String newLine = System.getProperty("line.separator");

        StringBuilder builder = new StringBuilder("{");

        builder.append(
                String.format(
                        "size=%d, total=%e, ind=%e, pair=%e%n",
                        getMarks().size(),
                        getEnergyTotal(),
                        getIndividual().getEnergyTotal(),
                        getPair().getEnergyTotal()));

        builder.append(getIndividual().describeMarks(getMarks()));
        builder.append(getPair().toString());

        builder.append("}");
        builder.append(newLine);

        return builder.toString();
    }

    /**
     * Gets the number of marks in the collection.
     *
     * @return the number of marks as an int
     */
    public final int size() {
        return marks.size();
    }
}
