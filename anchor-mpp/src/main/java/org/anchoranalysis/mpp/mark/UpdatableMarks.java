/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.mark;

import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.shared.SharedFeatures;
import org.anchoranalysis.mpp.mark.voxelized.memo.MemoForIndex;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;

/**
 * An interface for managing a set of marks that can be updated.
 * 
 * <p>This interface provides methods for initializing, adding, exchanging, and removing marks
 * from a set, while maintaining any necessary internal state or data structures.</p>
 */
public interface UpdatableMarks {

    /**
     * Initializes the updatable marks with necessary context.
     *
     * @param memo the memo for indexing
     * @param energyStack the energy stack
     * @param logger the logger for any logging operations
     * @param sharedFeatures shared features to be used
     * @throws InitializeException if initialization fails
     */
    void initUpdatableMarks(
            MemoForIndex memo,
            EnergyStack energyStack,
            Logger logger,
            SharedFeatures sharedFeatures)
            throws InitializeException;

    /**
     * Adds a new mark to the existing set of marks.
     *
     * @param marksExisting the existing set of marks
     * @param newMark the new mark to be added
     * @throws UpdateMarkSetException if the addition operation fails
     */
    void add(MemoForIndex marksExisting, VoxelizedMarkMemo newMark) throws UpdateMarkSetException;

    /**
     * Exchanges an existing mark with a new one.
     *
     * @param memo the memo for indexing
     * @param oldMark the mark to be replaced
     * @param indexOldMark the index of the old mark
     * @param newMark the new mark to replace the old one
     * @throws UpdateMarkSetException if the exchange operation fails
     */
    void exchange(
            MemoForIndex memo,
            VoxelizedMarkMemo oldMark,
            int indexOldMark,
            VoxelizedMarkMemo newMark)
            throws UpdateMarkSetException;

    /**
     * Removes a mark from the existing set of marks.
     *
     * @param marksExisting the existing set of marks
     * @param mark the mark to be removed
     * @throws UpdateMarkSetException if the removal operation fails
     */
    void remove(MemoForIndex marksExisting, VoxelizedMarkMemo mark) throws UpdateMarkSetException;
}