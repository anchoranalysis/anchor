/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.proposer;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.mark.CompatibleWithMark;
import org.anchoranalysis.anchor.mpp.pair.PairPxlMarkMemo;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.bean.annotation.GroupingRoot;

@GroupingRoot
public abstract class MarkSplitProposer extends ProposerBean<MarkSplitProposer>
        implements CompatibleWithMark {

    public abstract Optional<PairPxlMarkMemo> propose(
            VoxelizedMarkMemo mark, ProposerContext context, CfgGen cfgGen)
            throws ProposalAbnormalFailureException;
}
