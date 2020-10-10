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

import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.io.output.outputter.Outputter;

/**
 * Creates a sequence of outputs of the same type by repeatedly calling a generator.
 * 
 * <p>This usually occurs in a sub-directory, but not necessarily.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class OutputSequence {

    public static <T> OutputSequenceIncremental<T> createIncremental(
            OutputSequenceDirectory directory, Generator<T> generator, InputOutputContext context) {

        OutputSequenceParameters<T> parameters = createParameters(directory, generator,context.getOutputter());
        
        return new OutputSequenceIncremental<>(
                new OutputSequenceIncrementalChecked<>(parameters, 0),
                context.getErrorReporter());
    }
    
    public static <T> OutputSequenceNonIncrementalLogged<T> createNonIncrementalLogged(
        OutputSequenceDirectory directory, Generator<T> generator, InputOutputContext context) {
        OutputSequenceNonIncrementalChecked<T> checked = createNonIncrementalChecked(directory, generator, context);
        return new OutputSequenceNonIncrementalLogged<>(checked, context.getErrorReporter());
    }
    
    public static <T> OutputSequenceNonIncrementalChecked<T> createNonIncrementalChecked(
            OutputSequenceDirectory directory, Generator<T> generator, InputOutputContext context) {
        return new OutputSequenceNonIncrementalChecked<>( createParameters(directory, generator, context.getOutputter()) );
    }
    
    /**
     * Generates a file for each element in a stream in a sub-directory with an increasing integer sequence in the file-name.
     * 
     * @param <T> element-type in stream
     * @param stream the items to generate separate files
     * @param generator the generator to use for creating each file
     * @param context where writing occurs to
     */
    public static <T> void writeStreamAsSubdirectory(
            OutputSequenceDirectory directory, 
            Stream<T> stream,
            Generator<T> generator,
            InputOutputContext context) {

        OutputSequenceIncremental<T> sequenceWriter = createIncremental(directory, generator,context);

        sequenceWriter.start();
        try {
            stream.forEach(sequenceWriter::add);
        } finally {
            sequenceWriter.end();
        }
    }
    
    private static <T> OutputSequenceParameters<T> createParameters(OutputSequenceDirectory directory, Generator<T> generator, Outputter outputter) {
        return new OutputSequenceParameters<>(outputter.getChecked(), directory, generator);
    }
}
