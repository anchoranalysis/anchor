package ch.ethz.biol.cell.mpp.cfgtoobjmaskwriter;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.overlay.writer.OverlayWriter;

public abstract class OverlayWriterWithRegionMembership extends OverlayWriter {

	public abstract RegionMembershipWithFlags getRegionMembership();
}
