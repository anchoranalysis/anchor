/*-
 * #%L
 * anchor-image-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.anchoranalysis.image.io.generator.raster;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.io.rasterwriter.RasterWriteOptions;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Uses a delegate raster-generator and optionally applies a conversions before certain operations.
 *
 * <p>Specifically, a conversion may occur before:
 *
 * <ul>
 *   <li>Calling {@link #assignElement(Object)}.
 *   <li>Calling {@link #transform()}.
 * </ul>
 *
 * @author Owen Feehan
 * @param <S> delegate iteration-type
 * @param <T> generator iteration-type (the iteration-type that is publicly exposed)
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class RasterGeneratorDelegateToRaster<S, T> extends RasterGenerator<T> {

    // START REQUIRED ARGUMENTS
    /** The delegate. */
    private final RasterGenerator<S> delegate;
    // END REQUIRED ARGUMENTS

    private T element;

    @Override
    public boolean isRGB() {
        return delegate.isRGB();
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return delegate.createManifestDescription();
    }

    @Override
    public void start() throws OutputWriteFailedException {
        super.start();
        delegate.start();
    }

    @Override
    public void end() throws OutputWriteFailedException {
        delegate.end();
        element = null;
    }

    @Override
    public RasterWriteOptions rasterWriteOptions() {
        return delegate.rasterWriteOptions();
    }

    @Override
    public void assignElement(T element) throws SetOperationFailedException {
        this.element = element;

        try {
            getDelegate().assignElement(convertBeforeAssign(element));
        } catch (OperationFailedException e) {
            throw new SetOperationFailedException(e);
        }
    }

    @Override
    public final T getElement() {
        return element;
    }

    @Override
    public Stack transform() throws OutputWriteFailedException {
        return convertBeforeTransform(getDelegate().transform());
    }

    /**
     * Converts an element before setting it on the {@code delegate}.
     *
     * @param element element to convert
     * @return converted element
     * @throws OperationFailedException if anything goes wrong during conversion
     */
    protected abstract S convertBeforeAssign(T element) throws OperationFailedException;

    /**
     * Converts an element before calling {@link #transform()} on the {@code delegate}.
     *
     * @param stack stack to convert
     * @return converted element
     */
    protected abstract Stack convertBeforeTransform(Stack stack);

    protected RasterGenerator<S> getDelegate() {
        return delegate;
    }
}
