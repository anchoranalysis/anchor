/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.instantstate;

import lombok.EqualsAndHashCode;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgWithNRGTotal;
import org.anchoranalysis.core.index.SingleIndexCntr;

@EqualsAndHashCode(callSuper = true)
public class CfgWithNrgTotalInstantState extends SingleIndexCntr {

    private CfgWithNRGTotal cfg;

    public CfgWithNrgTotalInstantState(int iter, CfgWithNRGTotal cfg) {
        super(iter);
        this.cfg = cfg;
    }

    public CfgWithNRGTotal getCfg() {
        return cfg;
    }

    public void setCfg(CfgWithNRGTotal cfg) {
        this.cfg = cfg;
    }
}
