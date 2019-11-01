package ch.ethz.biol.cell.core;

import org.anchoranalysis.anchor.mpp.bean.MPPBean;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.list.OrderedFeatureList;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.params.ICompatibleWith;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

import ch.ethz.biol.cell.mpp.mark.check.CheckException;

public abstract class CheckMark extends MPPBean<CheckMark> implements ICompatibleWith, OrderedFeatureList {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8676574486398286141L;
	
	/**
	 * Called before any calls to check()
	 * @param nrgStack TODO
	 */
	public void start(NRGStackWithParams nrgStack) throws OperationFailedException {

	}
	
	/**
	 * Checks a mark
	 * 
	 * @param mark
	 * @param regionMap
	 * @param nrgStack
	 * @param featureSession session (nb nrgStack can be added to featureSession)
	 * @return
	 */
	public abstract boolean check(Mark mark, RegionMap regionMap, NRGStackWithParams nrgStack) throws CheckException;

	/**
	 * Called after any calls to check()
	 */
	public void end() {
		
	}

}
