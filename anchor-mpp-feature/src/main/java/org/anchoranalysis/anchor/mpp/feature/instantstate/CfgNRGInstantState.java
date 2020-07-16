/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.instantstate;

import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;
import org.anchoranalysis.core.index.SingleIndexCntr;

public abstract class CfgNRGInstantState extends SingleIndexCntr {

    public CfgNRGInstantState(int iter) {
        super(iter);
    }

    public abstract CfgNRG getCfgNRG();
}
