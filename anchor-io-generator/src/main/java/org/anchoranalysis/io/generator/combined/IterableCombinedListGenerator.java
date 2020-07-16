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
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.MultipleFileTypeGenerator;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

// We can probably have a more efficient implementation by not using the CombinedListGenerator as a
// delegate
//  but we leave it for now
// Should always have at least one item added
public class IterableCombinedListGenerator<T>
        implements MultipleFileTypeGenerator, IterableGenerator<T> {

    private final CombinedListGenerator delegate = new CombinedListGenerator();

    private final List<IterableGenerator<T>> list = new ArrayList<>();

    public IterableCombinedListGenerator(NameValue<IterableGenerator<T>> namedGenerator) {
        add(namedGenerator.getValue(), Optional.of(namedGenerator.getName()));
    }

    public IterableCombinedListGenerator(Stream<NameValue<IterableGenerator<T>>> namedGenerators) {
        namedGenerators.forEach(item -> add(item.getValue(), Optional.of(item.getName())));
        checkNonEmptyList();
    }

    @SafeVarargs
    public IterableCombinedListGenerator(IterableGenerator<T>... generator) {
        Arrays.stream(generator).forEach(gen -> add(gen, Optional.empty()));
        checkNonEmptyList();
    }

    @Override
    public void start() throws OutputWriteFailedException {
        for (IterableGenerator<T> generator : list) {
            generator.start();
        }
    }

    @Override
    public void write(OutputNameStyle outputNameStyle, BoundOutputManager outputManager)
            throws OutputWriteFailedException {
        delegate.write(outputNameStyle, outputManager);
    }

    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle,
            String index,
            BoundOutputManager outputManager)
            throws OutputWriteFailedException {
        return delegate.write(outputNameStyle, index, outputManager);
    }

    @Override
    public Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings) {
        return delegate.getFileTypes(outputWriteSettings);
    }

    @Override
    public T getIterableElement() {
        if (list.isEmpty()) {
            throw new AnchorFriendlyRuntimeException("List of generators is empty");
        }
        return list.get(0).getIterableElement();
    }

    @Override
    public void setIterableElement(T element) throws SetOperationFailedException {

        for (IterableGenerator<T> generator : list) {
            generator.setIterableElement(element);
        }
    }

    @Override
    public Generator getGenerator() {
        return this;
    }

    public void add(String name, IterableGenerator<T> element) {
        add(element, Optional.of(name));
    }

    @Override
    public void end() throws OutputWriteFailedException {
        for (IterableGenerator<T> generator : list) {
            generator.end();
        }
    }

    private void add(IterableGenerator<T> element, Optional<String> name) {
        list.add(element);
        delegate.add(element.getGenerator(), name);
    }

    private void checkNonEmptyList() {
        Preconditions.checkArgument(!list.isEmpty());
    }
}
