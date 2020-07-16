/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.kernel;

import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;

public class CfgGenContext {

    private CfgGen cfgGen;

    public CfgGenContext(CfgGen cfgGen) {
        super();
        this.cfgGen = cfgGen;
    }

    public CfgGen getCfgGen() {
        return cfgGen;
    }
}
