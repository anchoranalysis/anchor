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

import java.util.Optional;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.functional.CheckedStream;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPattern;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPatternIntegerSuffix;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPatternStringSuffix;
import org.anchoranalysis.io.manifest.sequencetype.IncreasingIntegers;
import org.anchoranalysis.io.manifest.sequencetype.IncrementingIntegers;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.manifest.sequencetype.StringsWithoutOrder;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Creates and starts a {@link OutputSequence} with a particular generator and context.
 * 
 * <p>This usually occurs in a subdirectory (relative to {@link #outputter}, but not necessarily.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class OutputSequenceFactory<T> {

    /** The generator to be repeatedly called for writing each element in the sequence. */
    private Generator<T> generator;

    /** The root directory where writing occurs to, often adding a subdirectory for the sequence. */
    private OutputterChecked outputter;
    
    /**
     * Writes elements to {@code directory}, with an incrementing integer in the filename.
     * 
     * <p>The integer in the filename starts at 0.
     * 
     * <p>The eventual filename written becomes {@code $prefix$index.$extension}.
     * 
     * @param pattern how the output of the sequence looks on the file-system and in the manifest.
     * @return a newly created sequence (which must be closed with {@link OutputSequence#close()} after usage.
     * @throws OutputWriteFailedException if any outputting cannot be successfully completed.
     */
    public OutputSequenceIncrementing<T> incrementingByOne(
            OutputPatternIntegerSuffix pattern) throws OutputWriteFailedException {
        return incrementingByOne(pattern, 0);
    }
    
    /**
     * Writes elements to the current directory, with an incrementing integer in the filename.
     * 
     * <p>The integer in the filename starts at 0.
     * 
     * <p>The eventual filename written becomes {@code $prefix$index.$extension}.
     * 
     * @param outputName the associated output-name
     * @param prefix a prefix to include before each filename that is outputted.
     * @param numberDigits the number of digits (adding trailing zeros) for the integer part of the output-name.
     * @return a newly created sequence (which must be closed with {@link OutputSequence#close()} after usage.
     * @throws OutputWriteFailedException if any outputting cannot be successfully completed.
     */
    public OutputSequenceIncrementing<T> incrementingByOneCurrentDirectory(String outputName, String prefix, int numberDigits) throws OutputWriteFailedException {
        OutputPatternIntegerSuffix pattern = new OutputPatternIntegerSuffix(
            outputName,
            true,
            prefix,
            numberDigits,
            true,
            Optional.empty()
        );
        return incrementingByOne(pattern, 0);
    }
    
    public OutputSequenceIndexed<T,Integer> incrementingIntegers(
            OutputPatternIntegerSuffix pattern, int startIndex, int incrementSize) throws OutputWriteFailedException {
        return indexed(pattern, new IncrementingIntegers(startIndex, incrementSize) );
    }
    
    public OutputSequenceIndexed<T,Integer> increasingIntegers(
            OutputPatternIntegerSuffix pattern) throws OutputWriteFailedException {
        return new OutputSequenceIndexed<>( bind(pattern), new IncreasingIntegers() );
    }


    /**
     * Writes elements (indexed by strings) to {@code directory}, without any order in the sequence.
     * 
     * <p>Each index must be unique.
     * 
     * <p>The eventual filename written becomes {@code $prefix$index.$extension}.
     *  
     * @param pattern how the output of the sequence looks on the file-system and in the manifest.
     * @return a newly created sequence (which must be closed with {@link OutputSequence#close()} after usage.
     * @throws OutputWriteFailedException if any outputting cannot be successfully completed.
     */
    public OutputSequenceIndexed<T,String> withoutOrder(
            OutputPatternStringSuffix pattern) throws OutputWriteFailedException {
        return indexed(pattern, new StringsWithoutOrder() );
    }
    
    /**
     * Writes elements (indexed by strings) to the current directory, without any order in the sequence.
     * 
     * <p>Each index must be unique.
     * 
     * <p>The eventual filename written becomes {@code $index.$extension} without any prefix or suffix.
     * 
     * @param outputName the output-name used in rules to determine if the output is enabled or not.
     * @return a newly created sequence (which must be closed with {@link OutputSequence#close()} after usage.
     * @throws OutputWriteFailedException if any outputting cannot be successfully completed.
     */
    public OutputSequenceIndexed<T,String> withoutOrderCurrentDirectory(
            String outputName) throws OutputWriteFailedException {
        OutputPatternStringSuffix pattern = new OutputPatternStringSuffix(
            outputName,
            true
        );
        return withoutOrder(pattern);
    }
    
    /**
     * Writes file for each element in a stream with an incrementing integer sequence in the outputted file-name.
     * 
     * <p>The integer in the filename starts at 0.
     * 
     * @param pattern how the output of the sequence looks on the file-system and in the manifest.
     * @param stream the items to generate separate files
     * @throws OutputWriteFailedException if any output fails to be written.
     */
    public void incrementingByOneStream(OutputPatternIntegerSuffix pattern, Stream<T> stream) throws OutputWriteFailedException {

        try (OutputSequenceIncrementing<T> sequenceWriter = incrementingByOne(pattern)) {
            CheckedStream.forEach(stream, OutputWriteFailedException.class, sequenceWriter::add);
        }
    }
    
    private <S> OutputSequenceIndexed<T,S> indexed(
            OutputPattern pattern, SequenceType<S> sequenceType) throws OutputWriteFailedException {
        return new OutputSequenceIndexed<>( bind(pattern), sequenceType );
    }
    
    private OutputSequenceIncrementing<T> incrementingByOne(
            OutputPatternIntegerSuffix pattern, int startIndex) throws OutputWriteFailedException {
        return new OutputSequenceIncrementing<>( bind(pattern), startIndex);
    }
    
    private BoundOutputter<T> bind(OutputPattern pattern) {
        return new BoundOutputter<>(outputter, pattern, generator);
    }
}
