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
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.mpp.feature.energy.scheme.EnergySchemeWithSharedFeatures;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;

/**
 * A collection of marks with an associated (total) energy.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MarksWithTotalEnergy implements Serializable {

    /** */
    private static final long serialVersionUID = -7008599104878130986L;

    /** The marks */
    @Getter @Setter private MarkCollection marks;

    /** Associated Energy Scheme, which should include the EnergySavedPairs */
    @Getter private transient EnergySchemeWithSharedFeatures energyScheme;

    /** The pre-annealed total energy */
    @Getter @Setter private double energyTotal = 0;

    public MarksWithTotalEnergy(MarkCollection marks, EnergySchemeWithSharedFeatures energyScheme) {
        this.marks = marks;
        this.energyScheme = energyScheme;
        this.energyTotal = 0;
    }

    /**
     * Make a shallow copy
     *
     * @return a newly created object with identical contents (all member fields reused)
     */
    public MarksWithTotalEnergy shallowCopy() {
        return new MarksWithTotalEnergy(this.marks, this.energyScheme, this.energyTotal);
    }

    /**
     * Make a deep copy (except the {@code energyScheme} which is reused).
     *
     * @return a newly created object with identical contents (some member fields duplicated, some
     *     reused)
     */
    public MarksWithTotalEnergy deepCopy() {
        return new MarksWithTotalEnergy(this.marks.deepCopy(), this.energyScheme, this.energyTotal);
    }

    public void add(VoxelizedMarkMemo voxelizedMark) {
        replaceWithShallowCopy(marksCopy -> marksCopy.add(voxelizedMark.getMark()));
    }

    public void remove(int index) {
        // As none of our updates involve the memo list, we can do
        //  this operate after the other remove operations
        replaceWithShallowCopy(marksCopy -> marksCopy.remove(index));
    }

    // calculates a new energy and configuration based upon a mark at a particular index
    //   changing into new mark
    public void exchange(int index, VoxelizedMarkMemo newMark) {
        replaceWithShallowCopy(marksCopy -> marksCopy.exchange(index, newMark.getMark()));
    }

    public final int size() {
        return marks.size();
    }

    public Mark get(int index) {
        return marks.get(index);
    }

    private void replaceWithShallowCopy(Consumer<MarkCollection> operationAfterCopy) {

        // We shallow copy the existing configuration
        MarkCollection marksCopy = marks.shallowCopy();
        operationAfterCopy.accept(marksCopy);

        // We adopt the copy
        this.marks = marksCopy;
    }
}
