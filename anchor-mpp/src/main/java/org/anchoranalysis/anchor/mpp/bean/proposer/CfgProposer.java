/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.proposer;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.mark.CompatibleWithMark;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.bean.annotation.GroupingRoot;

// Proposes an entire configuration
@GroupingRoot
public abstract class CfgProposer extends ProposerBean<CfgProposer> implements CompatibleWithMark {

    public abstract Optional<Cfg> propose(CfgGen cfgGen, ProposerContext context)
            throws ProposalAbnormalFailureException;
}
