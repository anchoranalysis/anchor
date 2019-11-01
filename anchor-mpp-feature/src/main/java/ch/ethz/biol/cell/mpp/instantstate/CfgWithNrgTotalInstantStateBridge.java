package ch.ethz.biol.cell.mpp.instantstate;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.index.GetOperationFailedException;

// Bridges CfgNRGInstantState to CfgInstantState
public class CfgWithNrgTotalInstantStateBridge implements IObjectBridge<CfgWithNrgTotalInstantState, OverlayedInstantState> {

	
	
	@Override
	public OverlayedInstantState bridgeElement(CfgWithNrgTotalInstantState sourceObject) throws GetOperationFailedException {
		
		if (sourceObject==null) {
			throw new GetOperationFailedException("invalid index");
		}
		
		if (sourceObject.getCfg()==null) {
			return new OverlayedInstantState(sourceObject.getIndex(), new OverlayCollection() );
		}
		
		RegionMembershipWithFlags regionMembership = RegionMapSingleton.instance().membershipWithFlagsForIndex( GlobalRegionIdentifiers.SUBMARK_INSIDE );
		OverlayCollection oc = OverlayCollectionMarkFactory.createWithoutColor(
			sourceObject.getCfg().getCfg(),
			regionMembership
		);
			
		return new OverlayedInstantState(sourceObject.getIndex(), oc );
	}
	
	

}
