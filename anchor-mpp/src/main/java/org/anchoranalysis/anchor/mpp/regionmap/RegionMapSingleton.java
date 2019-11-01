package org.anchoranalysis.anchor.mpp.regionmap;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipAnd;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;

// A Default RegionMap
public class RegionMapSingleton {

	private static RegionMap regionMap = null;
	
	public static synchronized RegionMap instance() {
		if (regionMap==null) {
			regionMap = new RegionMap();
			regionMap.getList().add( new RegionMembershipAnd(GlobalRegionIdentifiers.SUBMARK_INSIDE) );
			regionMap.getList().add( new RegionMembershipAnd(GlobalRegionIdentifiers.SUBMARK_SHELL) );
			regionMap.getList().add( new RegionMembershipAnd(GlobalRegionIdentifiers.SUBMARK_CORE) );
			regionMap.getList().add( new RegionMembershipAnd(GlobalRegionIdentifiers.SUBMARK_OUTSIDE) );
			regionMap.getList().add( new RegionMembershipAnd(GlobalRegionIdentifiers.SUBMARK_CORE_INNER) );
			regionMap.getList().add( new RegionMembershipAnd(GlobalRegionIdentifiers.SUBMARK_SHELL_OUTSIDE) );
		}
		return regionMap;
	}
}
