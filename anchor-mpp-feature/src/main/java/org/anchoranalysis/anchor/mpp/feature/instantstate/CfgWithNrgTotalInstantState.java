package org.anchoranalysis.anchor.mpp.feature.instantstate;

import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgWithNrgTotal;
import org.anchoranalysis.core.index.SingleIndexCntr;

public class CfgWithNrgTotalInstantState extends SingleIndexCntr {

	private CfgWithNrgTotal cfg;
	
	public CfgWithNrgTotalInstantState(int iter, CfgWithNrgTotal cfg) {
		super(iter);
		this.cfg = cfg;
	}

	public CfgWithNrgTotal getCfg() {
		return cfg;
	}

	public void setCfg(CfgWithNrgTotal cfg) {
		this.cfg = cfg;
	}

}
