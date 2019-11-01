package org.anchoranalysis.anchor.mpp.bean.proposer;

import org.anchoranalysis.anchor.mpp.params.ICompatibleWith;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.anchor.mpp.proposer.visualization.ICreateProposalVisualization;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;
import org.anchoranalysis.bean.annotation.GroupingRoot;

import anchor.provider.bean.ProposalAbnormalFailureException;

/*
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import anchor.provider.bean.ProposerBean;

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
