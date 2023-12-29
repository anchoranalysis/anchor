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

package org.anchoranalysis.io.generator.combined;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.identifier.name.NameValue;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.MultipleFileTypeGenerator;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.writer.ElementOutputter;

/**
 * Several generators combined together with a common element-type.
 *
 * <p>One generator must always exist. Zero generators is never allowed.
 *
 * <p>Each generator is associated with a unique output-name.
 *
 * @author Owen Feehan
 * @param <T> element-type
 */
@NoArgsConstructor
public class CombinedListGenerator<T> implements MultipleFileTypeGenerator<T> {

    /** The list of generators that are combined together. */
    private final CombinedList<T> list = new CombinedList<>();

    /**
     * Create from a <i>single</i> named generator.
     *
     * @param namedGenerator the generator with an associated name.
     */
    public CombinedListGenerator(NameValue<Generator<T>> namedGenerator) {
        add(namedGenerator.getValue(), Optional.of(namedGenerator.getName()));
    }

    /**
     * Create from <i>multiple</i> named generators.
     *
     * @param generator the generators, each with an associated name.
     */
    @SafeVarargs
    public CombinedListGenerator(Generator<T>... generator) {
        Arrays.stream(generator).forEach(gen -> add(gen, Optional.empty()));
        checkNonEmptyList();
    }

    /**
     * Create from a <i>stream</i> of named generators.
     *
     * @param namedGenerators the stream of generators, each with an associated name.
     */
    public CombinedListGenerator(Stream<NameValue<Generator<T>>> namedGenerators) {
        namedGenerators.forEach(item -> add(item.getValue(), Optional.of(item.getName())));
        checkNonEmptyList();
    }

    @Override
    public void write(T element, OutputNameStyle outputNameStyle, ElementOutputter outputter)
            throws OutputWriteFailedException {
        list.write(element, outputNameStyle, outputter);
    }

    @Override
    public void writeWithIndex(
            T element,
            String index,
            IndexableOutputNameStyle outputNameStyle,
            ElementOutputter outputter)
            throws OutputWriteFailedException {
        list.writeWithIndex(element, index, outputNameStyle, outputter);
    }

    /** Adds a generator with an optional name. */
    private void add(Generator<T> generator, Optional<String> name) {
        list.add(generator, name);
    }

    /** Throw an exception if the list is empty. */
    private void checkNonEmptyList() {
        Preconditions.checkArgument(!list.isEmpty());
    }
}
