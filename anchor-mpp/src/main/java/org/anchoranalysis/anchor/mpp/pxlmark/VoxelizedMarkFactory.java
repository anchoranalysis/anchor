package org.anchoranalysis.anchor.mpp.pxlmark;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.feature.nrg.NRGStack;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class VoxelizedMarkFactory {

	public static VoxelizedMarkHistogram create(Mark mark, NRGStack stack, RegionMap regionMap) {
		return new VoxelizedMarkHistogram(mark, stack, regionMap);
	}
}
