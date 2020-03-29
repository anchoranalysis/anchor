package org.anchoranalysis.anchor.mpp.feature.instantstate;

import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;

public class CfgNRGNonHandleInstantState extends CfgNRGInstantState {

	private CfgNRG cfgNRG;
	
	public CfgNRGNonHandleInstantState(int iter, CfgNRG cfgNRG) {
		super(iter);
		this.cfgNRG = cfgNRG;
	}

	@Override
	public CfgNRG getCfgNRG() {
		return cfgNRG;
	}
}
