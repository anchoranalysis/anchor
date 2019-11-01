package ch.ethz.biol.cell.mpp.instantstate;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.index.GetOperationFailedException;

import ch.ethz.biol.cell.mpp.cfg.Cfg;

// Bridges CfgNRGInstantState to OverlayedInstantState
public class CfgNRGInstantStateBridge implements IObjectBridge<CfgNRGInstantState, OverlayedInstantState> {

	private RegionMembershipWithFlags regionMembership;
		
	public CfgNRGInstantStateBridge(RegionMembershipWithFlags regionMembership) {
		super();
		this.regionMembership = regionMembership;
	}

	@Override
	public OverlayedInstantState bridgeElement(CfgNRGInstantState sourceObject) throws GetOperationFailedException {
		
		if (sourceObject==null) {
			throw new GetOperationFailedException("invalid index");
		}
		
		if (sourceObject.getCfgNRG()==null) {
			return new OverlayedInstantState(sourceObject.getIndex(), new OverlayCollection() );
		}
		
		Cfg cfg = sourceObject.getCfgNRG().getCfg();
		
		if (cfg==null) {
			cfg = new Cfg();
		}
		
		OverlayCollection oc = OverlayCollectionMarkFactory.createWithoutColor(
			cfg,
			regionMembership
		);
		
		return new OverlayedInstantState(sourceObject.getIndex(), oc);
	}
	
	

}
