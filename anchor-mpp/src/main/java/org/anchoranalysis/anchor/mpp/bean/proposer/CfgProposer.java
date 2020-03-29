package org.anchoranalysis.anchor.mpp.bean.proposer;

import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.params.ICompatibleWith;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.bean.annotation.GroupingRoot;

// Proposes an entire configuration
@GroupingRoot
public abstract class CfgProposer extends ProposerBean<CfgProposer> implements ICompatibleWith {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5343121289470299500L;

	public abstract Cfg propose( CfgGen cfgGen, ProposerContext context ) throws ProposalAbnormalFailureException;
}
