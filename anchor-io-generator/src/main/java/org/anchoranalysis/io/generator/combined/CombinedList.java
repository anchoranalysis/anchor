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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.writer.ElementWriter;

/**
 * A helper list of {@link ElementWriter}s used in {@link CombinedListGenerator}.
 *
 * @author Owen Feehan
 * @param <T> element-type
 */
class CombinedList<T> {

    private List<OptionalNameValue<Generator<T>>> list = new ArrayList<>();

    public Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings)
            throws OperationFailedException {

        List<FileType> all = new ArrayList<>();

        for (OptionalNameValue<Generator<T>> namedGenerator : list) {
            Optional<FileType[]> fileTypeArray =
                    namedGenerator.getValue().getFileTypes(outputWriteSettings);
            fileTypeArray.ifPresent(
                    fileTypes -> {
                        for (int i = 0; i < fileTypes.length; i++) {
                            all.add(fileTypes[i]);
                        }
                    });
        }

        if (!all.isEmpty()) {
            return Optional.of(all.toArray(new FileType[] {}));
        } else {
            return Optional.empty();
        }
    }

    public void write(T element, OutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException {

        for (OptionalNameValue<Generator<T>> namedGenerator : list) {
            namedGenerator.getName().ifPresent(outputNameStyle::setOutputName);
            namedGenerator.getValue().write(element, outputNameStyle, outputter);
        }
    }

    public int writeWithIndex(
            T element,
            String index,
            IndexableOutputNameStyle outputNameStyle,
            OutputterChecked outputter)
            throws OutputWriteFailedException {

        int maxWritten = -1;
        for (OptionalNameValue<Generator<T>> namedGenerator : list) {

            if (namedGenerator.getName().isPresent()) {
                outputNameStyle = outputNameStyle.duplicate();
                outputNameStyle.setOutputName(namedGenerator.getName().get()); // NOSONAR
            }

            int numberWritten =
                    namedGenerator
                            .getValue()
                            .writeWithIndex(element, index, outputNameStyle, outputter);
            maxWritten = Math.max(maxWritten, numberWritten);
        }

        return maxWritten;
    }

    /**
     * Adds a generator with an optional-name.
     *
     * <p>Note that everything should have a name, or nothing should. Please don't mix. This is not
     * currently checked.
     *
     * @param generator the generator to add
     * @param name optional-name, which if included, is set as the output-name for the generator
     */
    public void add(Generator<T> generator, Optional<String> name) {
        list.add(new OptionalNameValue<>(name, generator));
    }
}
