package org.anchoranalysis.mpp.sgmn.bean.optscheme;

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
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.mpp.sgmn.bean.optscheme.termination.TriggerTerminationCondition;
import org.anchoranalysis.mpp.sgmn.kernel.CfgGenContext;
import org.anchoranalysis.mpp.sgmn.kernel.KernelCalcContext;

import ch.ethz.biol.cell.mpp.DualStack;
import ch.ethz.biol.cell.mpp.cfg.CfgGen;
import ch.ethz.biol.cell.mpp.nrg.NRGSchemeWithSharedFeatures;

public class OptSchemeInitContext {

	private String experimentDescription;
	private NRGSchemeWithSharedFeatures nrgScheme;
	private DualStack dualStack;
	private TriggerTerminationCondition triggerTerminationCondition;
	private ExperimentExecutionArguments experimentArguments;
	private LogErrorReporter logger;
	private BoundOutputManagerRouteErrors outputManager;
	private RandomNumberGenerator re;
	private CfgGen cfgGen;
	
	public OptSchemeInitContext(String experimentDescription, NRGSchemeWithSharedFeatures nrgScheme,
			DualStack dualStack, TriggerTerminationCondition triggerTerminationCondition,
			ExperimentExecutionArguments experimentArguments, LogErrorReporter logger,
			BoundOutputManagerRouteErrors outputManager, RandomNumberGenerator re, CfgGen cfgGen) {
		super();
		this.experimentDescription = experimentDescription;
		this.nrgScheme = nrgScheme;
		this.dualStack = dualStack;
		this.triggerTerminationCondition = triggerTerminationCondition;
		this.experimentArguments = experimentArguments;
		this.logger = logger;
		this.outputManager = outputManager;
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
			new ErrorNodeNull()
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
	public ExperimentExecutionArguments getExperimentArguments() {
		return experimentArguments;
	}
	public LogErrorReporter getLogger() {
		return logger;
	}
	public BoundOutputManagerRouteErrors getOutputManager() {
		return outputManager;
	}

	public CfgGen getCfgGen() {
		return cfgGen;
	}
}
