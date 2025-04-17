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

package org.anchoranalysis.mpp.bean.proposer;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.mpp.mark.CompatibleWithMark;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.mpp.proposer.ProposerContext;
import org.anchoranalysis.spatial.point.Point3f;

/** An abstract base class for proposing merges between marks. */
@GroupingRoot
public abstract class MarkMergeProposer extends ProposerBean<MarkMergeProposer>
        implements CompatibleWithMark {

    /**
     * Proposes a merge between two marks.
     *
     * @param mark1 the first mark to merge
     * @param mark2 the second mark to merge
     * @param context the context for the proposal
     * @return an Optional containing the merged mark, or empty if no merge is proposed
     * @throws ProposalAbnormalFailureException if the proposal fails abnormally
     */
    public abstract Optional<Mark> propose(
            VoxelizedMarkMemo mark1, VoxelizedMarkMemo mark2, ProposerContext context)
            throws ProposalAbnormalFailureException;

    /**
     * Gets the points associated with the first mark from the last proposal.
     *
     * @return an Optional containing a list of points, or empty if no points are available
     */
    public Optional<List<Point3f>> getLastPoints1() {
        return Optional.empty();
    }

    /**
     * Gets the points associated with the second mark from the last proposal.
     *
     * @return an Optional containing a list of points, or empty if no points are available
     */
    public Optional<List<Point3f>> getLastPoints2() {
        return Optional.empty();
    }
}
