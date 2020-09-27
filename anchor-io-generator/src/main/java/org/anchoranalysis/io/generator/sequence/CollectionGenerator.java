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

package org.anchoranalysis.io.generator.sequence;

import java.util.Collection;
import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.OutputterChecked;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * Writes a collection of items via a generator into a subfolder.
 * 
 * @author Owen Feehan
 *
 * @param <T> element-type
 */
@RequiredArgsConstructor @AllArgsConstructor
public class CollectionGenerator<T> implements Generator<Collection<T>> {

    // START REQUIRED ARGUMENTS
    private final String subfolderName;
    private final Generator<T> generator;
    private final OutputterChecked outputter;
    private final int numberDigits;
    private final boolean selective;
    // END REQUIRED ARGUMENTS
    
    private Collection<T> collection;

    @Override
    public void write(OutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException {

        writeCollection(subfolderName, outputNameStyle.deriveIndexableStyle(numberDigits), 0);
    }

    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle,
            String index,
            OutputterChecked outputter)
            throws OutputWriteFailedException {

        // In this context, we take the index as an indication of the first id to use - and assume
        // the String index is a number
        int indexInt = Integer.parseInt(index);
        return writeCollection(subfolderName, outputNameStyle, indexInt);
    }

    private int writeCollection(
            String subfolderName, IndexableOutputNameStyle outputNameStyle, int startIndex)
            throws OutputWriteFailedException {

        assert (collection != null);

        // We start with id with 0
        GeneratorSequenceIncrementalWriter<T> sequenceWriter =
                new GeneratorSequenceIncrementalWriter<>(
                        outputter,
                        subfolderName,
                        outputNameStyle,
                        generator,
                        startIndex,
                        selective);

        int numWritten = 0;

        sequenceWriter.start();
        for (T element : collection) {
            sequenceWriter.add(element);
            numWritten++;
        }
        sequenceWriter.end();

        return numWritten;
    }

    @Override
    public Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings) throws OperationFailedException {
        return generator.getFileTypes(outputWriteSettings);
    }

    @Override
    public Collection<T> getElement() {
        return collection;
    }

    @Override
    public void assignElement(Collection<T> element) {
        this.collection = element;
    }

    @Override
    public void start() throws OutputWriteFailedException {
        generator.start();
    }

    @Override
    public void end() throws OutputWriteFailedException {
        generator.end();
    }
}
