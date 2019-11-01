package ch.ethz.biol.cell.mpp.nrg;

import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;
import org.anchoranalysis.feature.calc.params.FeatureCalcParamsNRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public class NRGElemPairCalcParams extends FeatureCalcParamsNRGStack {

	private PxlMarkMemo obj1;
	private PxlMarkMemo obj2;
	private transient NRGStackWithParams nrgStack;
	
	public NRGElemPairCalcParams(
		PxlMarkMemo obj1,
		PxlMarkMemo obj2,
		NRGStackWithParams nrgStack
	) {
		super();
		this.obj1 = obj1;
		this.obj2 = obj2;
		this.nrgStack = nrgStack;
		assert obj1!=null;
		assert obj2!=null;
		assert nrgStack!=null;
	}
	public PxlMarkMemo getObj1() {
		return obj1;
	}
	public void setObj1(PxlMarkMemo obj1) {
		this.obj1 = obj1;
	}
	public PxlMarkMemo getObj2() {
		return obj2;
	}
	public void setObj2(PxlMarkMemo obj2) {
		this.obj2 = obj2;
	}
	public NRGStackWithParams getNrgStack() {
		return nrgStack;
	}
	public void setNrgStack(NRGStackWithParams nrgStack) {
		this.nrgStack = nrgStack;
	}
}
