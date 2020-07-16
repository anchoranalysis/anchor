/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.proposer;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.anchor.mpp.mark.CompatibleWithMark;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.core.geometry.Point3f;

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
