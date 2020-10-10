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
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.manifest.sequencetype.SetSequenceType;
import org.anchoranalysis.io.namestyle.StringSuffixOutputNameStyle;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.InputOutputContext;

/**
 * Creates and starts a {@link OutputSequence} with a particular generator and context.
 * 
 * <p>This usually occurs in a sub-directory (relative to {@link #context}, but not necessarily.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class OutputSequenceFactory<T> {

    /** Te generator to be repeatedly called for writing each element in the sequence. */
    private Generator<T> generator;

    /** The root director where writing occurs to, often adding a sub-directory for the sequence. */
    private InputOutputContext context;
    
    public OutputSequenceIncremental<T> incremental(
            OutputSequenceDirectory directory) throws OutputWriteFailedException {
        OutputSequenceIncremental<T> sequence = incrementalUnstarted(directory);
        sequence.start();
        return sequence;
    }
    
    public OutputSequenceNonIncremental<T> nonIncremental(
            OutputSequenceDirectory directory, SequenceType sequenceType) throws OutputWriteFailedException {
        OutputSequenceNonIncremental<T> sequence = nonIncrementalUnstarted(directory);
        sequence.start(sequenceType);
        return sequence;
    }
    
    /**
     * 
     * <p>The sequence is also <i>started</i> after being created.
     * 
     * @param prefix
     * @return
     * @throws OutputWriteFailedException
     */
    public OutputSequenceNonIncremental<T> nonIncrementalCurrentDirectory(
            String prefix) throws OutputWriteFailedException {
        OutputSequenceDirectory sequenceDirectory = new OutputSequenceDirectory(
            Optional.empty(),
            new StringSuffixOutputNameStyle(prefix, "%s"),
            true,
            Optional.empty()
        );

        // TODO it would be nicer to reflect the real sequence type, than just using a set of
        // indexes
        return nonIncremental(sequenceDirectory, new SetSequenceType());
    }
    
    /**
     * Generates a file for each element in a stream in a sub-directory with an increasing integer sequence in the file-name.
     * 
     * @param stream the items to generate separate files
     * @throws OutputWriteFailedException if any output fails to be written.
     */
    public void incrementalStream(
            OutputSequenceDirectory directory, 
            Stream<T> stream) throws OutputWriteFailedException {

        OutputSequenceIncremental<T> sequenceWriter = incremental(directory);
        try {
            CheckedStream.forEach(stream, OutputWriteFailedException.class, sequenceWriter::add);
        } finally {
            sequenceWriter.end();
        }
    }
    
    private OutputSequenceIncremental<T> incrementalUnstarted(
            OutputSequenceDirectory directory) {
        return new OutputSequenceIncremental<>( bind(directory), 0);
    }
  
    private OutputSequenceNonIncremental<T> nonIncrementalUnstarted(
          OutputSequenceDirectory directory) {
      return new OutputSequenceNonIncremental<>( bind(directory) );
    }
  
    private BoundOutputter<T> bind(OutputSequenceDirectory directory) {
        return new BoundOutputter<>(context.getOutputter().getChecked(), directory, generator);
    }
}
