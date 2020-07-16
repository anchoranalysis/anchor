/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.instantstate;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.FunctionWithException;

// Bridges CfgNRGInstantState to OverlayedInstantState
public class CfgNRGInstantStateBridge
        implements FunctionWithException<
                CfgNRGInstantState, OverlayedInstantState, OperationFailedException> {

    private RegionMembershipWithFlags regionMembership;

    public CfgNRGInstantStateBridge(RegionMembershipWithFlags regionMembership) {
        super();
        this.regionMembership = regionMembership;
    }

    @Override
    public OverlayedInstantState apply(CfgNRGInstantState sourceObject)
            throws OperationFailedException {

        if (sourceObject == null) {
            throw new OperationFailedException("The sourceObject is null. Invalid index");
        }

        if (sourceObject.getCfgNRG() == null) {
            return new OverlayedInstantState(sourceObject.getIndex(), new OverlayCollection());
        }

        Cfg cfg = sourceObject.getCfgNRG().getCfg();

        if (cfg == null) {
            cfg = new Cfg();
        }

        OverlayCollection oc =
                OverlayCollectionMarkFactory.createWithoutColor(cfg, regionMembership);

        return new OverlayedInstantState(sourceObject.getIndex(), oc);
    }
}
