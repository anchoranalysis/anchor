package org.anchoranalysis.mpp.sgmn.bean.optscheme;



/*
 * #%L
 * anchor-mpp-sgmn
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

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.mpp.sgmn.bean.kernel.proposer.KernelProposer;
import org.anchoranalysis.mpp.sgmn.optscheme.OptTerminatedEarlyException;

import ch.ethz.biol.cell.mpp.feedback.FeedbackReceiver;
import ch.ethz.biol.cell.mpp.pair.ListUpdatableMarkSetCollection;


/**
 * 
 * @author FEEHANO
 *
 * @param <S> state returned from algorithm, and reported to the outside world
 * @param <U> type of kernel proposer
 */
public abstract class OptScheme<S,U> extends AnchorBean<OptScheme<S,U>> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5243971680667015706L;

	// Find optimum configuration
	public abstract S findOpt(
		KernelProposer<U> kernelProposer,
		ListUpdatableMarkSetCollection updatableMarkSetCollection,
		FeedbackReceiver<S> feedbackReceiver,
		OptSchemeInitContext initContext
	) throws OptTerminatedEarlyException;
}
