package org.anchoranalysis.anchor.mpp.feature.input.memo;

import java.util.Optional;

import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;
import org.anchoranalysis.feature.input.FeatureInputNRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public class FeatureInputSingleMemo extends FeatureInputNRGStack {

	private PxlMarkMemo pxlPartMemo;
	
	public FeatureInputSingleMemo(
		PxlMarkMemo pxlPartMemo,
		NRGStackWithParams nrgStack
	) {
		this(
			pxlPartMemo,
			Optional.of(nrgStack)
		);
	}
	
	public FeatureInputSingleMemo(
		PxlMarkMemo pxlPartMemo,
		Optional<NRGStackWithParams> nrgStack
	) {
		super(nrgStack);
		this.pxlPartMemo = pxlPartMemo;
	}

	public PxlMarkMemo getPxlPartMemo() {
		return pxlPartMemo;
	}

	public void setPxlPartMemo(PxlMarkMemo pxlPartMemo) {
		this.pxlPartMemo = pxlPartMemo;
	}
}
