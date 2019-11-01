package ch.ethz.biol.cell.mpp.cfgtoobjmaskwriter;

import org.anchoranalysis.anchor.overlay.writer.OverlayWriter;

import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMembershipWithFlags;

public abstract class OverlayWriterWithRegionMembership extends OverlayWriter {

	public abstract RegionMembershipWithFlags getRegionMembership();
}
