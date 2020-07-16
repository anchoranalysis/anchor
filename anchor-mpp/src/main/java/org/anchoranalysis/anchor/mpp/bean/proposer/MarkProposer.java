/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.proposer;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.mark.CompatibleWithMark;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.anchor.mpp.proposer.visualization.CreateProposalVisualization;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.bean.annotation.GroupingRoot;

// Proposes a mark
@GroupingRoot
public abstract class MarkProposer extends ProposerBean<MarkProposer>
        implements CompatibleWithMark {

    // The inputMark's attributes become changed in accordance to the internal rules of the proposer
    public abstract boolean propose(VoxelizedMarkMemo inputMark, ProposerContext context)
            throws ProposalAbnormalFailureException;

    public abstract Optional<CreateProposalVisualization> proposalVisualization(boolean detailed);
}
