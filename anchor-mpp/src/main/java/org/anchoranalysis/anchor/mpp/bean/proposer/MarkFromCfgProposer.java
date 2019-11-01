package org.anchoranalysis.anchor.mpp.bean.proposer;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.params.ICompatibleWith;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;

import ch.ethz.biol.cell.mpp.cfg.Cfg;

public abstract class MarkFromCfgProposer extends ProposerBean<MarkFromCfgProposer> implements ICompatibleWith  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3752172396013943340L;
	
	public abstract Mark markFromCfg( Cfg cfg, ProposerContext context ) throws ProposalAbnormalFailureException;

}
