/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.stack.DisplayStack;

/** Stores an NRGStack together with a background stack */
public class DualStack {
    private NRGStackWithParams nrgStack;
    private DisplayStack bgStack;

    public DualStack(NRGStackWithParams stackBoth) throws CreateException {
        this(stackBoth, DisplayStack.create(stackBoth.getNrgStack().asStack()));
    }

    public DualStack(NRGStackWithParams nrgStack, DisplayStack bgStack) {
        super();
        this.nrgStack = nrgStack;
        this.bgStack = bgStack;
    }

    public NRGStackWithParams getNrgStack() {
        return nrgStack;
    }

    public DisplayStack getBgStack() {
        return bgStack;
    }
}
