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
import org.anchoranalysis.core.geometry.Point3f;
import org.anchoranalysis.mpp.mark.CompatibleWithMark;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.mpp.proposer.ProposerContext;

@GroupingRoot
public abstract class MarkMergeProposer extends ProposerBean<MarkMergeProposer>
        implements CompatibleWithMark {

    // Returns a merged mark or NULL
    public abstract Optional<Mark> propose(
            VoxelizedMarkMemo mark1, VoxelizedMarkMemo mark2, ProposerContext context)
            throws ProposalAbnormalFailureException;

    // A debug method for optionally associating points with the last proposal made
    public Optional<List<Point3f>> getLastPoints1() {
        return Optional.empty();
    }

    // A debug method for optionally associating points with the last proposal made
    public Optional<List<Point3f>> getLastPoints2() {
        return Optional.empty();
    }
}
