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
 * Allows us to call an {@code IterableSingleFileTypeGenerator<V,S>} as if it was an {@code
 * IterableSingleFileTypeGenerator<T,S>} using an function to connect the two
 *
 * @author Owen Feehan
 * @param <S> generator-type
 * @param <T> exposed-iterator type
 * @param <V> hidden-iterator-type
 */
@RequiredArgsConstructor
public class IterableSingleFileTypeGeneratorBridge<S, T, V> extends SingleFileTypeGenerator<T,S> implements IterableSingleFileTypeGenerator<T, S>, IterableGenerator<T> {

    // START REQUIRED ARGUMENTS
    private final IterableSingleFileTypeGenerator<V, S> internalGenerator;
    private final CheckedFunction<T, V, ? extends Throwable> elementBridge;
    // END REQUIRED ARGUMENTS

    private T element;

    @Override
    public T getIterableElement() {
        return this.element;
    }

    @Override
    public void setIterableElement(T element) throws SetOperationFailedException {
        this.element = element;
        try {
            V bridgedElement = elementBridge.apply(element);
            internalGenerator.setIterableElement(bridgedElement);
        } catch (Exception e) {
            throw new SetOperationFailedException(e);
        }
    }

    @Override
    public SingleFileTypeGenerator<T,S> getGenerator() {
        return this;
    }

    @Override
    public void start() throws OutputWriteFailedException {
        internalGenerator.start();
    }

    @Override
    public void end() throws OutputWriteFailedException {
        internalGenerator.end();
    }


    @Override
    public String getFileExtension(OutputWriteSettings outputWriteSettings)
            throws OperationFailedException {
        return internalGenerator.getGenerator().getFileExtension(outputWriteSettings);
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return internalGenerator.getGenerator().createManifestDescription();
    }

    @Override
    public S transform() throws OutputWriteFailedException {
        return internalGenerator.getGenerator().transform();
    }

    @Override
    public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException {
        internalGenerator.getGenerator().writeToFile(outputWriteSettings, filePath);
    }
}
