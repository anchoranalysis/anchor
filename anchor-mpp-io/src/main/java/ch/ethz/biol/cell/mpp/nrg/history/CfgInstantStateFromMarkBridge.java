package ch.ethz.biol.cell.mpp.nrg.history;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.bridge.IObjectBridgeIndex;
import org.anchoranalysis.core.index.GetOperationFailedException;

import ch.ethz.biol.cell.mpp.cfg.Cfg;


// Creates CfgInstantState from marks with an index
public class CfgInstantStateFromMarkBridge implements IObjectBridgeIndex<Mark, OverlayedInstantState> {
	
	private RegionMembershipWithFlags regionMembership;
		
	public CfgInstantStateFromMarkBridge(RegionMembershipWithFlags regionMembership) {
		super();
		this.regionMembership = regionMembership;
	}

	@Override
	public OverlayedInstantState bridgeElement(int index, Mark sourceObject)
			throws GetOperationFailedException {
		
		Cfg cfg = new Cfg();
		cfg.add( sourceObject);
		
		OverlayCollection oc = OverlayCollectionMarkFactory.createWithoutColor(
			cfg,
			regionMembership
		);
		return new OverlayedInstantState(index, oc);
	}
}