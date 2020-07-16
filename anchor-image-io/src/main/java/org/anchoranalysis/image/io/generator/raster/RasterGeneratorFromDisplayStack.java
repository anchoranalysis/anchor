/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster;

import java.util.Optional;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * @author Owen Feehan
 * @param <T> iteration-type
 */
public class RasterGeneratorFromDisplayStack<T> extends RasterGenerator
        implements IterableObjectGenerator<T, Stack> {

    private IterableObjectGenerator<T, DisplayStack> delegate;
    private boolean rgb;

    public RasterGeneratorFromDisplayStack(
            IterableObjectGenerator<T, DisplayStack> delegate, boolean rgb) {
        super();
        this.delegate = delegate;
        this.rgb = rgb;
    }

    @Override
    public void start() throws OutputWriteFailedException {
        delegate.start();
    }

    @Override
    public void end() throws OutputWriteFailedException {
        delegate.end();
    }

    @Override
    public ObjectGenerator<Stack> getGenerator() {
        return this;
    }

    @Override
    public boolean isRGB() {
        return rgb;
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return delegate.getGenerator().createManifestDescription();
    }

    @Override
    public T getIterableElement() {
        return delegate.getIterableElement();
    }

    @Override
    public void setIterableElement(T element) throws SetOperationFailedException {
        delegate.setIterableElement(element);
    }

    @Override
    public Stack generate() throws OutputWriteFailedException {
        return delegate.getGenerator().generate().createImgStack(false);
    }
}
