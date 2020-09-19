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

import java.util.Optional;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.CheckedStream;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Exposes a {@code IterableGenerator<S>} as if it was an {@code IterableGenerator<T>}.
 *
 * @author Owen Feehan
 * @param <S> source-type
 * @param <T> destination-type
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class IterableGeneratorBridge<S, T> implements Generator<S>, IterableGenerator<S> {

    // START REQUIRED ARGUMENTS
    /** The generator that accepts the destination type */
    private final IterableGenerator<T> generator;

    /** Maps the source-type to one or more instances of the destination type */
    private final CheckedFunction<S, Stream<T>, ?> bridge;
    // END REQUIRED ARGUMENTS

    /**
     * Creates a bridge that maps ONE-TO-ONE from source to destination (i.e. one call to the
     * generator for each source item)
     *
     * @param <S> source-type
     * @param <T> destination-type
     * @param generator the generator that accepts the destination type
     * @param bridge maps a source-item to one destination-item
     * @return a generator that accepts source-types as iterators, but actually calls a generator
     *     that uses destination types
     */
    public static <S, T> IterableGeneratorBridge<S, T> createOneToOne(
            IterableGenerator<T> generator, CheckedFunction<S, T, ?> bridge) {
        return new IterableGeneratorBridge<>(generator, item -> Stream.of(bridge.apply(item)));
    }

    /**
     * Creates a bridge that maps ONE-TO-MANY from source to destination (i.e. one or more calls to
     * the generator for each source item)
     *
     * @param <S> source-type
     * @param <T> destination-type
     * @param generator the generator that accepts the destination type
     * @param bridge maps a source-item to one or more destination-items
     * @return a generator that accepts source-types as iterators, but actually calls a generator
     *     that uses destination types
     */
    public static <S, T> IterableGeneratorBridge<S, T> createOneToMany(
            IterableGenerator<T> generator, CheckedFunction<S, Stream<T>, ?> bridge) {
        return new IterableGeneratorBridge<>(generator, bridge);
    }

    private S element;

    @Override
    public S getIterableElement() {
        return this.element;
    }

    @Override
    public void setIterableElement(S element) throws SetOperationFailedException {
        this.element = element;
        try {
            CheckedStream.forEach(
                    bridge.apply(element),
                    SetOperationFailedException.class,
                    generator::setIterableElement);
        } catch (Exception e) {
            throw new SetOperationFailedException(e);
        }
    }

    @Override
    public Generator<S> getGenerator() {
        return this;
    }

    @Override
    public void start() throws OutputWriteFailedException {
        generator.start();
    }

    @Override
    public void end() throws OutputWriteFailedException {
        generator.end();
    }

    @Override
    public void write(OutputNameStyle outputNameStyle, BoundOutputManager outputManager)
            throws OutputWriteFailedException {
        generator.getGenerator().write(outputNameStyle, outputManager);
    }

    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle,
            String index,
            BoundOutputManager outputManager)
            throws OutputWriteFailedException {
        return generator.getGenerator().write(outputNameStyle, index, outputManager);
    }

    @Override
    public Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings) throws OperationFailedException {
        return generator.getGenerator().getFileTypes(outputWriteSettings);
    }
}
