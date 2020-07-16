/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.nrg.saved;

import java.io.Serializable;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoCollection;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGSchemeWithSharedFeatures;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.nrg.NRGStack;

// Saves particular features for all items
public class NRGSavedAll implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    private double nrgTotal;

    public void calc(
            MemoCollection pxlMarkMemoList,
            NRGSchemeWithSharedFeatures nrgScheme,
            NRGStack nrgRaster)
            throws FeatureCalcException {
        nrgTotal = nrgScheme.calcElemAllTotal(pxlMarkMemoList, nrgRaster).getTotal();
    }

    public double getNRGTotal() {
        return nrgTotal;
    }

    public NRGSavedAll shallowCopy() {
        NRGSavedAll out = new NRGSavedAll();
        out.nrgTotal = nrgTotal;
        return out;
    }

    public NRGSavedAll deepCopy() {
        return shallowCopy();
    }
}
