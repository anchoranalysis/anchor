/*-
 * #%L
 * anchor-io-generator
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

package org.anchoranalysis.io.generator;

import lombok.RequiredArgsConstructor;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Allows us to call an {@code SingleFileTypeGenerator<V,S>} as if it was an {@code
 * SingleFileTypeGenerator<T,S>} using an function to connect the two.
 *
 * @author Owen Feehan
 * @param <S> generator-type
 * @param <T> exposed-iterator type
 * @param <V> hidden-iterator-type
 */
@RequiredArgsConstructor
public class SingleFileTypeGeneratorBridge<S, T, V> extends SingleFileTypeGeneratorWithElement<T,S> {

    // START REQUIRED ARGUMENTS
    private final SingleFileTypeGenerator<V, S> delegate;
    private final CheckedFunction<T, V, ? extends Throwable> elementBridge;
    // END REQUIRED ARGUMENTS

    @Override
    public void assignElement(T element) throws SetOperationFailedException {
        super.assignElement(element);
        try {
            V bridgedElement = elementBridge.apply(element);
            delegate.assignElement(bridgedElement);
        } catch (Exception e) {
            throw new SetOperationFailedException(e);
        }
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
    public String getFileExtension(OutputWriteSettings outputWriteSettings)
            throws OperationFailedException {
        return delegate.getFileExtension(outputWriteSettings);
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return delegate.createManifestDescription();
    }

    @Override
    public S transform() throws OutputWriteFailedException {
        return delegate.transform();
    }

    @Override
    public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException {
        delegate.writeToFile(outputWriteSettings, filePath);
    }
}