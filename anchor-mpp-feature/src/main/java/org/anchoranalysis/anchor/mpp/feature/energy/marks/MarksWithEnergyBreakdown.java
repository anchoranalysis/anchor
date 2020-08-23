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

package org.anchoranalysis.anchor.mpp.feature.energy.marks;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.feature.energy.saved.EnergySavedAll;
import org.anchoranalysis.anchor.mpp.feature.energy.saved.EnergySavedInd;
import org.anchoranalysis.anchor.mpp.feature.energy.saved.EnergySavedPairs;
import org.anchoranalysis.anchor.mpp.feature.energy.scheme.EnergySchemeWithSharedFeatures;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoCollection;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoList;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkCollection;
import org.anchoranalysis.anchor.mpp.mark.set.UpdateMarkSetException;
import org.anchoranalysis.anchor.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParams;

/** 
 * Marks with both the total energy and a breakdown by clique
 **/
@AllArgsConstructor
public class MarksWithEnergyBreakdown implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    // START REQUIRED ARGUMENTS
    private final MarksWithTotalEnergy marks;

    @Getter private EnergySavedInd individual;

    /** Every combination of interactions between marks and the associated energy */
    @Getter private transient EnergySavedPairs pair;

    /** Certain features are stored for every object, so that we can reference them in our calculations for the 'all' component */
    @Getter @Setter private transient EnergySavedAll all;
    // END REQUIRED ARGUMENTS

    public MarksWithEnergyBreakdown(MarksWithTotalEnergy marks) {
        this.marks = marks;
    }

    // The initial calculation of the Energy, thereafter it can be updated
    public void init() throws NamedFeatureCalculateException {
        this.individual = new EnergySavedInd();
        try {
            this.pair = new EnergySavedPairs(marks.getEnergyScheme().createAddCriteria());
            this.all = new EnergySavedAll();
        } catch (CreateException e) {
            throw new NamedFeatureCalculateException(e);
        }
    }

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

    // This should be accessed read-only
    public MarksWithTotalEnergy getMarksWithTotalEnergy() {
        return marks;
    }

    public void updateTotal(MemoCollection pxlMarkMemoList, EnergyStackWithoutParams stack)
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

    public MarksWithEnergyBreakdown shallowCopy() {
        return new MarksWithEnergyBreakdown(
                marks.shallowCopy(),
                individual.shallowCopy(),
                pair.shallowCopy(),
                all.shallowCopy());
    }

    public MarksWithEnergyBreakdown deepCopy() {
        return new MarksWithEnergyBreakdown(
                marks.deepCopy(),
                individual.deepCopy(),
                pair.deepCopy(),
                all.deepCopy());
    }

    public double getEnergyTotal() {
        return marks.getEnergyTotal();
    }

    public MarkCollection getMarks() {
        return marks.getMarks();
    }

    public EnergySchemeWithSharedFeatures getEnergyScheme() {
        return marks.getEnergyScheme();
    }

    public void add(MemoCollection wrapperInd, VoxelizedMarkMemo newPxlMarkMemo, EnergyStackWithoutParams stack)
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

    public void remove(MemoCollection wrapperInd, VoxelizedMarkMemo markToRemove, EnergyStackWithoutParams stack)
            throws NamedFeatureCalculateException {

        int index = wrapperInd.getIndexForMemo(markToRemove);
        assert (index != -1);
        remove(wrapperInd, index, markToRemove, stack);
    }

    public void remove(MemoCollection wrapperInd, int index, VoxelizedMarkMemo markToRemove, EnergyStackWithoutParams stack)
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

    public void removeTwo(MemoCollection wrapperInd, int index1, int index2, EnergyStackWithoutParams energyStack)
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

    // calculates a new energy and configuration based upon a mark at a particular index
    //   changing into new mark
    public void exchange(
            MemoCollection wrapperInd,
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
                        energyStack.getEnergyStack(),
                        marks.getEnergyScheme());
        try {
            VoxelizedMarkMemo oldPxlMarkMemo = wrapperInd.getMemoForMark(getMarks(), oldMark);
            getPair().exchange(wrapperInd, oldPxlMarkMemo, index, newPxlMarkMemo);
        } catch (UpdateMarkSetException e) {
            throw new NamedFeatureCalculateException(e);
        }

        assert (getPair().isMarksSpan(marks.getMarks()));

        // we can also calculate both afresh, but slower
        updateTotal(wrapperInd, energyStack.getEnergyStack());

        assert ((marks.getEnergyTotal()
                        - getIndividual().getEnergyTotal()
                        - getPair().getEnergyTotal())
                < 1e-6);
    }

    public void remove(int index) {
        marks.remove(index);
    }

    public void exchange(int index, VoxelizedMarkMemo newMark) {
        marks.exchange(index, newMark);
    }

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

    public final int size() {
        return marks.size();
    }
}
