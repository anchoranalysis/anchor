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

    private static final long serialVersionUID = -7008599104878130986L;

    /** The marks in the collection. */
    @Getter @Setter private MarkCollection marks;

    /** Associated Energy Scheme, which should include the EnergySavedPairs. */
    @Getter private transient EnergySchemeWithSharedFeatures energyScheme;

    /** The pre-annealed total energy. */
    @Getter @Setter private double energyTotal = 0;

    /**
     * Creates a new instance with the given marks and energy scheme.
     *
     * @param marks the collection of marks
     * @param energyScheme the energy scheme with shared features
     */
    public MarksWithTotalEnergy(MarkCollection marks, EnergySchemeWithSharedFeatures energyScheme) {
        this.marks = marks;
        this.energyScheme = energyScheme;
        this.energyTotal = 0;
    }

    /**
     * Makes a shallow copy of this instance.
     *
     * @return a newly created object with identical contents (all member fields reused)
     */
    public MarksWithTotalEnergy shallowCopy() {
        return new MarksWithTotalEnergy(this.marks, this.energyScheme, this.energyTotal);
    }

    /**
     * Makes a deep copy of this instance (except the {@code energyScheme} which is reused).
     *
     * @return a newly created object with identical contents (some member fields duplicated, some reused)
     */
    public MarksWithTotalEnergy deepCopy() {
        return new MarksWithTotalEnergy(this.marks.deepCopy(), this.energyScheme, this.energyTotal);
    }

    /**
     * Adds a voxelized mark to the collection.
     *
     * @param voxelizedMark the voxelized mark to add
     */
    public void add(VoxelizedMarkMemo voxelizedMark) {
        replaceWithShallowCopy(marksCopy -> marksCopy.add(voxelizedMark.getMark()));
    }

    /**
     * Removes a mark at the specified index from the collection.
     *
     * @param index the index of the mark to remove
     */
    public void remove(int index) {
        replaceWithShallowCopy(marksCopy -> marksCopy.remove(index));
    }

    /**
     * Exchanges a mark at a particular index with a new mark.
     *
     * @param index the index of the mark to exchange
     * @param newMark the new voxelized mark to replace the existing mark
     */
    public void exchange(int index, VoxelizedMarkMemo newMark) {
        replaceWithShallowCopy(marksCopy -> marksCopy.exchange(index, newMark.getMark()));
    }

    /**
     * Gets the number of marks in the collection.
     *
     * @return the number of marks
     */
    public final int size() {
        return marks.size();
    }

    /**
     * Gets the mark at the specified index.
     *
     * @param index the index of the mark to retrieve
     * @return the {@link Mark} at the specified index
     */
    public Mark get(int index) {
        return marks.get(index);
    }

    /**
     * Replaces the current marks collection with a shallow copy after performing an operation.
     *
     * @param operationAfterCopy the operation to perform on the copied marks collection
     */
    private void replaceWithShallowCopy(Consumer<MarkCollection> operationAfterCopy) {
        MarkCollection marksCopy = marks.shallowCopy();
        operationAfterCopy.accept(marksCopy);
        this.marks = marksCopy;
    }
}