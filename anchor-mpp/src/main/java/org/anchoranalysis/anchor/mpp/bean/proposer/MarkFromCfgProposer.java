/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.proposer;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.mark.CompatibleWithMark;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;

public abstract class MarkFromCfgProposer extends ProposerBean<MarkFromCfgProposer>
        implements CompatibleWithMark {

    public abstract Optional<Mark> markFromCfg(Cfg cfg, ProposerContext context)
            throws ProposalAbnormalFailureException;
}
