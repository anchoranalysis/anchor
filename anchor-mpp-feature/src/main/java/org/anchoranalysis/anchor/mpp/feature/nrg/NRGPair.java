/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.nrg;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.feature.nrg.NRGTotal;

public class NRGPair {

    private final Pair<Mark> pair;

    private final NRGTotal nrg;

    public NRGPair(Pair<Mark> pair, NRGTotal nrg) {
        super();
        this.pair = pair;
        this.nrg = nrg;
    }

    public Pair<Mark> getPair() {
        return pair;
    }

    public NRGTotal getNRG() {
        return nrg;
    }
}
