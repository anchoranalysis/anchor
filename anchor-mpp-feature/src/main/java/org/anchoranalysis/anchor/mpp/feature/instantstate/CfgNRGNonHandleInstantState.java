/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.instantstate;

import lombok.EqualsAndHashCode;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;

@EqualsAndHashCode(callSuper = true)
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
