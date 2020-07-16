/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.cfg;

import org.anchoranalysis.anchor.mpp.bean.provider.MPPProvider;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.core.error.CreateException;

@GroupingRoot
public abstract class CfgProvider extends MPPProvider<CfgProvider, Cfg> {

    @Override
    public abstract Cfg create() throws CreateException;
}
