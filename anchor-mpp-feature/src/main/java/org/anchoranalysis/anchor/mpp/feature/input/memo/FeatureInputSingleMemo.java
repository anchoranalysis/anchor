package org.anchoranalysis.anchor.mpp.feature.input.memo;

import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;
import org.anchoranalysis.feature.input.FeatureInputNRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public class FeatureInputSingleMemo extends FeatureInputNRGStack {

	private PxlMarkMemo pxlPartMemo;
	
	public FeatureInputSingleMemo(
		PxlMarkMemo pxlPartMemo,
		NRGStackWithParams raster
	) {
		super(raster);
		this.pxlPartMemo = pxlPartMemo;
	}

	public PxlMarkMemo getPxlPartMemo() {
		return pxlPartMemo;
	}

	public void setPxlPartMemo(PxlMarkMemo pxlPartMemo) {
		this.pxlPartMemo = pxlPartMemo;
	}

}
