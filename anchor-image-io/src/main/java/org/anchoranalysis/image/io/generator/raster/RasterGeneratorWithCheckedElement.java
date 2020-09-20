package org.anchoranalysis.image.io.generator.raster;

import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Like a {@link RasterGeneratorWithElement} but {@link #assignElement(Object)} is not final and throws an exception.
 * 
 * @author Owen Feehan
 *
 * @param <T> iteration-type
 */
public abstract class RasterGeneratorWithCheckedElement<T> extends RasterGenerator<T> {

    private T element;

    @Override
    public final T getElement() {
        return element;
    }

    @Override
    public void assignElement(T element) throws SetOperationFailedException {
        this.element = element;
    }
        
    @Override
    public void end() throws OutputWriteFailedException {
        // Don't keep element in memory any longer that necessary
        this.element = null;
    }
}
