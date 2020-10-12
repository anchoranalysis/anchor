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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.MultipleFileTypeGenerator;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Several generators combined together with a common iteration-type.
 *
 * <p>One generator must always exist. Zero generators is never allowed.
 *
 * @author Owen Feehan
 * @param <T> element-type
 */
public class CombinedListGenerator<T> implements MultipleFileTypeGenerator<T> {

    private final CombinedList delegate = new CombinedList();

    private final List<Generator<T>> list = new ArrayList<>();

    public CombinedListGenerator(NameValue<Generator<T>> namedGenerator) {
        add(namedGenerator.getValue(), Optional.of(namedGenerator.getName()));
    }

    public CombinedListGenerator(Stream<NameValue<Generator<T>>> namedGenerators) {
        namedGenerators.forEach(item -> add(item.getValue(), Optional.of(item.getName())));
        checkNonEmptyList();
    }

    @SafeVarargs
    public CombinedListGenerator(Generator<T>... generator) {
        Arrays.stream(generator).forEach(gen -> add(gen, Optional.empty()));
        checkNonEmptyList();
    }

    @Override
    public void write(OutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException {
        delegate.write(outputNameStyle, outputter);
    }

    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle, String index, OutputterChecked outputter)
            throws OutputWriteFailedException {
        return delegate.write(outputNameStyle, index, outputter);
    }

    @Override
    public Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings)
            throws OperationFailedException {
        return delegate.getFileTypes(outputWriteSettings);
    }

    @Override
    public T getElement() {
        if (list.isEmpty()) {
            throw new AnchorFriendlyRuntimeException("List of generators is empty");
        }
        return list.get(0).getElement();
    }

    @Override
    public void assignElement(T element) throws SetOperationFailedException {

        for (Generator<T> generator : list) {
            generator.assignElement(element);
        }
    }

    public void add(String name, Generator<T> element) {
        add(element, Optional.of(name));
    }
    
    private void add(Generator<T> element, Optional<String> name) {
        list.add(element);
        delegate.add(element, name);
    }

    private void checkNonEmptyList() {
        Preconditions.checkArgument(!list.isEmpty());
    }
}
