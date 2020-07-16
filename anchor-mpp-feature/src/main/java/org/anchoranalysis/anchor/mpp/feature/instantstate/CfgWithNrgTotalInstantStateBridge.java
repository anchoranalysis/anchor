/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.instantstate;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.FunctionWithException;

public class CfgWithNrgTotalInstantStateBridge
        implements FunctionWithException<
                CfgWithNrgTotalInstantState, OverlayedInstantState, OperationFailedException> {

    @Override
    public OverlayedInstantState apply(CfgWithNrgTotalInstantState sourceObject)
            throws OperationFailedException {

        if (sourceObject == null) {
            throw new OperationFailedException("The sourceObject is null. Invalid index");
        }

        if (sourceObject.getCfg() == null) {
            return new OverlayedInstantState(sourceObject.getIndex(), new OverlayCollection());
        }

        RegionMembershipWithFlags regionMembership =
                RegionMapSingleton.instance()
                        .membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE);
        OverlayCollection oc =
                OverlayCollectionMarkFactory.createWithoutColor(
                        sourceObject.getCfg().getCfg(), regionMembership);

        return new OverlayedInstantState(sourceObject.getIndex(), oc);
    }
}
