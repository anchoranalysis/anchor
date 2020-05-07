package org.anchoranalysis.mpp.sgmn.optscheme;

import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGSchemeWithSharedFeatures;

/*-
 * #%L
 * anchor-mpp-sgmn
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNodeNull;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.mpp.sgmn.bean.optscheme.termination.TriggerTerminationCondition;
import org.anchoranalysis.mpp.sgmn.kernel.CfgGenContext;
import org.anchoranalysis.mpp.sgmn.kernel.KernelCalcContext;

public class OptSchemeInitContext {

	private String experimentDescription;
	private NRGSchemeWithSharedFeatures nrgScheme;
	private DualStack dualStack;
	private TriggerTerminationCondition triggerTerminationCondition;
	private BoundIOContext context;
	private RandomNumberGenerator re;
	private CfgGen cfgGen;
	
	public OptSchemeInitContext(
		String experimentDescription,
		NRGSchemeWithSharedFeatures nrgScheme,
		DualStack dualStack,
		TriggerTerminationCondition triggerTerminationCondition,
		BoundIOContext context,
		RandomNumberGenerator re,
		CfgGen cfgGen
	) {
		super();
		this.experimentDescription = experimentDescription;
		this.nrgScheme = nrgScheme;
		this.dualStack = dualStack;
		this.triggerTerminationCondition = triggerTerminationCondition;
		this.context = context;
		this.re = re;
		this.cfgGen = cfgGen;
	}
	
	
	public CfgGenContext cfgGenContext() {
		return new CfgGenContext(
			cfgGen
		);
	}
	
	
	public KernelCalcContext calcContext( CfgGenContext cfgGenContext ) {
		return new KernelCalcContext(
			cfgGenContext,
			dualStack.getNrgStack(),
			nrgScheme,
			re,
			ErrorNodeNull.instance()
		);
	}
	
	public String getExperimentDescription() {
		return experimentDescription;
	}
	public NRGSchemeWithSharedFeatures getNrgScheme() {
		return nrgScheme;
	}
	public DualStack getDualStack() {
		return dualStack;
	}
	public TriggerTerminationCondition getTriggerTerminationCondition() {
		return triggerTerminationCondition;
	}

	public LogErrorReporter getLogger() {
		return context.getLogger();
	}
	public BoundOutputManagerRouteErrors getOutputManager() {
		return context.getOutputManager();
	}

	public CfgGen getCfgGen() {
		return cfgGen;
	}

	public boolean isDebugEnabled() {
		return context.isDebugEnabled();
	}
}
