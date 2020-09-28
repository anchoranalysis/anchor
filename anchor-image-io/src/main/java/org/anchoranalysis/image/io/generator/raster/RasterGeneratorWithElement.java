package org.anchoranalysis.image.io.generator.raster;

import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public abstract class RasterGeneratorWithElement<T> extends RasterGenerator<T> {

    private T element;

    @Override
    public final T getElement() {
        return element;
    }

    @Override
    public final void assignElement(T element) {
        this.element = element;
    }

    @Override
    public void end() throws OutputWriteFailedException {
        // Don't keep element in memory any longer that necessary
        this.element = null;
    }
}
