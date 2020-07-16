/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster;

import java.util.Optional;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class MIPGenerator extends RasterGenerator implements IterableObjectGenerator<Stack, Stack> {

    private StackGenerator delegate;

    public MIPGenerator(boolean padIfNec, String manifestFunction) {
        delegate = new StackGenerator(padIfNec, manifestFunction);
    }

    public MIPGenerator(Stack element, boolean padIfNec, String manifestFunction) {
        delegate = new StackGenerator(element, padIfNec, manifestFunction);
    }

    @Override
    public boolean isRGB() {
        return delegate.isRGB();
    }

    @Override
    public Stack generate() throws OutputWriteFailedException {

        Stack stack = delegate.getIterableElement();
        return stack.maxIntensityProj();
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return delegate.createManifestDescription();
    }

    @Override
    public Stack getIterableElement() {
        return delegate.getIterableElement();
    }

    @Override
    public void setIterableElement(Stack element) {
        delegate.setIterableElement(element);
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
}
