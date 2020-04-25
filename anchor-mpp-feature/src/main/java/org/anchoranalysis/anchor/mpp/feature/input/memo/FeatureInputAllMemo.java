package org.anchoranalysis.anchor.mpp.feature.input.memo;

import java.util.Optional;

import org.anchoranalysis.anchor.mpp.feature.mark.MemoCollection;
import org.anchoranalysis.feature.input.FeatureInputNRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public class FeatureInputAllMemo extends FeatureInputNRGStack {

	private MemoCollection pxlMarkMemoList;
	
	public FeatureInputAllMemo(
		MemoCollection pxlMarkMemoList,
		NRGStackWithParams raster
	) {
		super(
			Optional.of(raster)
		);
		this.pxlMarkMemoList = pxlMarkMemoList;
	}

	public MemoCollection getPxlPartMemo() {
		return pxlMarkMemoList;
	}

	public void setPxlPartMemo(MemoCollection pxlPartMemoList) {
		this.pxlMarkMemoList = pxlPartMemoList;
	}
}
