/* (C)2020 */
package org.anchoranalysis.io.output.bean;

import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.RGBColor;

class ColorIndexModulo implements ColorIndex {

    private ColorIndex delegate;

    public ColorIndexModulo(ColorIndex delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    public RGBColor get(int i) {
        return delegate.get(i % numUniqueColors());
    }

    @Override
    public int numUniqueColors() {
        return delegate.numUniqueColors();
    }

    @Override
    public boolean has(int i) {
        return delegate.has(i % numUniqueColors());
    }
}
