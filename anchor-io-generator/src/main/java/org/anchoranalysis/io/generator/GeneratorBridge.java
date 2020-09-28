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
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Exposes a {@code Generator<S>} as if it was an {@code Generator<T>}.
 *
 * @author Owen Feehan
 * @param <S> source-type
 * @param <T> destination-type
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneratorBridge<S, T> implements Generator<S> {

    private S element;

    // START REQUIRED ARGUMENTS
    /** The generator that accepts the destination type */
    private final Generator<T> generator;

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
    public static <S, T> GeneratorBridge<S, T> createOneToOne(
            Generator<T> generator, CheckedFunction<S, T, ?> bridge) {
        return new GeneratorBridge<>(generator, item -> Stream.of(bridge.apply(item)));
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
    public static <S, T> GeneratorBridge<S, T> createOneToMany(
            Generator<T> generator, CheckedFunction<S, Stream<T>, ?> bridge) {
        return new GeneratorBridge<>(generator, bridge);
    }

    @Override
    public S getElement() {
        return this.element;
    }

    @Override
    public void assignElement(S element) throws SetOperationFailedException {
        this.element = element;
        try {
            CheckedStream.forEach(
                    bridge.apply(element),
                    SetOperationFailedException.class,
                    generator::assignElement);
        } catch (Exception e) {
            throw new SetOperationFailedException(e);
        }
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
    public void write(OutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException {
        generator.write(outputNameStyle, outputter);
    }

    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle, String index, OutputterChecked outputter)
            throws OutputWriteFailedException {
        return generator.write(outputNameStyle, index, outputter);
    }

    @Override
    public Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings)
            throws OperationFailedException {
        return generator.getFileTypes(outputWriteSettings);
    }
}
