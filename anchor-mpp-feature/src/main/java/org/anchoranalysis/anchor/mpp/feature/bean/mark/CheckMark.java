/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.bean.mark;

import org.anchoranalysis.anchor.mpp.bean.MPPBean;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.feature.error.CheckException;
import org.anchoranalysis.anchor.mpp.mark.CompatibleWithMark;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public abstract class CheckMark extends MPPBean<CheckMark> implements CompatibleWithMark {

    /**
     * Called before any calls to check()
     *
     * @param nrgStack
     */
    public void start(NRGStackWithParams nrgStack) throws OperationFailedException {}

    /**
     * Checks a mark
     *
     * @param mark
     * @param regionMap
     * @param nrgStack
     * @param featureSession session (nb nrgStack can be added to featureSession)
     * @return
     */
    public abstract boolean check(Mark mark, RegionMap regionMap, NRGStackWithParams nrgStack)
            throws CheckException;

    /** Called after any calls to check() */
    public void end() {}
}
