package ch.ethz.biol.cell.mpp.cfg.proposer;

import org.anchoranalysis.anchor.mpp.bean.proposer.ProposerBean;
import org.anchoranalysis.anchor.mpp.params.ICompatibleWith;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.bean.annotation.GroupingRoot;

import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.cfg.CfgGen;

// Proposes an entire configuration
@GroupingRoot
public abstract class CfgProposer extends ProposerBean<CfgProposer> implements ICompatibleWith {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5343121289470299500L;

	public abstract Cfg propose( CfgGen cfgGen, ProposerContext context ) throws ProposalAbnormalFailureException;
}
