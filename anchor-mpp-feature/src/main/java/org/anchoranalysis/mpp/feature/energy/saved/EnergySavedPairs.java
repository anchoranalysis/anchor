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

package org.anchoranalysis.mpp.feature.energy.saved;

import java.util.Set;
import lombok.Getter;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.graph.TypedEdge;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.mpp.feature.addcriteria.AddCriteria;
import org.anchoranalysis.mpp.feature.addcriteria.RandomCollectionWithAddCriteria;
import org.anchoranalysis.mpp.feature.energy.EnergyPair;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.mark.UpdatableMarks;
import org.anchoranalysis.mpp.mark.UpdateMarkSetException;
import org.anchoranalysis.mpp.mark.voxelized.memo.MemoForIndex;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;

public class EnergySavedPairs implements UpdatableMarks {

    /** Pairwise energy total */
    @Getter private double energyTotal;

    private RandomCollectionWithAddCriteria<EnergyPair> pairCollection;

    public EnergySavedPairs(AddCriteria<EnergyPair> addCriteria) {
        this.pairCollection = new RandomCollectionWithAddCriteria<>(EnergyPair.class);
        this.pairCollection.setAddCriteria(addCriteria);
    }

    public EnergySavedPairs shallowCopy() {
        EnergySavedPairs out = new EnergySavedPairs(this.pairCollection.getAddCriteria());
        out.pairCollection = this.pairCollection.shallowCopy();
        out.energyTotal = this.energyTotal;
        return out;
    }

    public EnergySavedPairs deepCopy() {
        EnergySavedPairs out = new EnergySavedPairs(this.pairCollection.getAddCriteria());
        out.pairCollection = this.pairCollection.deepCopy();
        out.energyTotal = this.energyTotal;
        return out;
    }

    @Override
    public void initUpdatableMarks(
            MemoForIndex pxlMarkMemoList,
            EnergyStack stack,
            Logger logger,
            SharedFeatureMulti sharedFeatures)
            throws InitializeException {

        this.pairCollection.initUpdatableMarks(pxlMarkMemoList, stack, logger, sharedFeatures);
        calculateTotalFresh();
    }

    // Calculates energy for all pairwise interactions freshly
    private void calculateTotalFresh() {
        energyTotal = 0;
        for (EnergyPair pair : pairCollection.createPairsUnique()) {
            energyTotal += pair.getEnergyTotal().getTotal();
        }
        assert !Double.isNaN(energyTotal);
    }

    private double totalEnergyForMark(Mark mark) {

        double total = 0;
        for (TypedEdge<Mark, EnergyPair> pair : this.pairCollection.getPairsFor(mark)) {
            total += pair.getPayload().getEnergyTotal().getTotal();
        }
        assert !Double.isNaN(total);

        return total;
    }

    @Override
    public void add(MemoForIndex pxlMarkMemoList, VoxelizedMarkMemo newMark)
            throws UpdateMarkSetException {

        this.pairCollection.add(pxlMarkMemoList, newMark);
        this.energyTotal += totalEnergyForMark(newMark.getMark());
        assert !Double.isNaN(energyTotal);
    }

    @Override
    public void remove(MemoForIndex marksExisting, VoxelizedMarkMemo mark)
            throws UpdateMarkSetException {

        // We calculate it's individual contribution
        this.energyTotal -= totalEnergyForMark(mark.getMark());
        this.pairCollection.remove(marksExisting, mark);
        assert !Double.isNaN(energyTotal);
    }

    // exchanges one mark with another
    @Override
    public void exchange(
            MemoForIndex memo,
            VoxelizedMarkMemo oldMark,
            int indexOldMark,
            VoxelizedMarkMemo newMark)
            throws UpdateMarkSetException {

        // We get a total for how the old mark interacts with the other marks
        double oldPairTotal = totalEnergyForMark(oldMark.getMark());

        this.pairCollection.exchange(memo, oldMark, indexOldMark, newMark);

        double newPairTotal = totalEnergyForMark(newMark.getMark());
        this.energyTotal = this.energyTotal - oldPairTotal + newPairTotal;
    }

    // Does the pairs hash only contains items contained in a particular configuration
    public boolean isMarksSpan(MarkCollection marks) {
        return pairCollection.isMarksSpan(marks);
    }

    @Override
    public String toString() {

        String newLine = System.getProperty("line.separator");

        StringBuilder s = new StringBuilder("{");

        s.append(newLine);

        // We list all the non-null energy components
        for (EnergyPair di : createPairsUnique()) {
            s.append(
                    String.format(
                            "%2d--%2d\tenergy=%e%n",
                            di.getPair().getSource().getIdentifier(),
                            di.getPair().getDestination().getIdentifier(),
                            di.getEnergyTotal().getTotal()));
        }

        s.append("}" + newLine);

        return s.toString();
    }

    public void assertValid() {
        assert !Double.isNaN(energyTotal);
    }

    public Set<EnergyPair> createPairsUnique() {
        return pairCollection.createPairsUnique();
    }
}
