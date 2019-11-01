package org.anchoranalysis.anchor.mpp.bean.proposer;

import org.anchoranalysis.anchor.mpp.params.ICompatibleWith;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.anchor.mpp.proposer.visualization.ICreateProposalVisualization;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;
import org.anchoranalysis.bean.annotation.GroupingRoot;

// Proposes a mark
@GroupingRoot
public abstract class MarkProposer extends ProposerBean<MarkProposer> implements ICompatibleWith
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8973822953715696606L;
	
	// The inputMark's attributes become changed in accordance to the internal rules of the proposer
	public abstract boolean propose(
		PxlMarkMemo inputMark,
		ProposerContext context
	) throws ProposalAbnormalFailureException;
	
	public abstract ICreateProposalVisualization proposalVisualization(boolean detailed);
}
