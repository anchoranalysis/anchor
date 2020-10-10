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
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.namestyle.IntegerSuffixOutputNameStyle;
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
@AllArgsConstructor
public class OutputSequence {

    /** Name of sub-directory to place sequence in (which is also used as the outputName) */
    private final String subdirectoryName;
    
    /**
     * Prefix on each file-name.
     *
     * <p>The full-name has an increment number appended e.g. <code>$prefix_000000.tif</code> etc.
     */
    private final String prefix;
    
    /**
     * The number of digits in the numeric part of the output-name.
     */
    private final int numberDigits;
    
    /**
     * The description used for the sub-directory in the manifest, or one is automatically generated if not defined.
     */
    private final Optional<ManifestDescription> subdirectoryManifestDescription;

    /** Check if individual sequence items should be allowed to be outputted or not. */
    private final boolean checkIfAllowed;
    
    /**
     * Create for a particular sub-directory and number of digits..
     * 
     * @param subdirectoryName name of sub-directory to place sequence in (which is also used as the outputName and prefix)
     * @param numberDigits the number of digits in the numeric part of the output-name.
     */
    public OutputSequence(String subdirectoryName, int numberDigits) {
        this.subdirectoryName = subdirectoryName;
        this.prefix = subdirectoryName;
        this.numberDigits = numberDigits;
        this.subdirectoryManifestDescription = Optional.empty();
        this.checkIfAllowed = false;
    }
    
    /**
     * Create for a particular sub-directory and prefix.
     * 
     * @param subdirectoryName name of sub-directory to place sequence in (which is also used as the outputName)
     * @param prefix prefix on each file-name
     */
    public OutputSequence(String subdirectoryName, String prefix) {
        this.subdirectoryName = subdirectoryName;
        this.prefix = prefix;
        this.numberDigits = 6;
        this.subdirectoryManifestDescription = Optional.empty();
        this.checkIfAllowed = false;
    }
    
    public OutputSequence addSubdirectoryManifestDescription( ManifestDescription manifestDescription ) {
        return new OutputSequence(subdirectoryName, prefix, numberDigits, Optional.of(manifestDescription), checkIfAllowed );
    }
    
    public OutputSequence selective() {
        return new OutputSequence(subdirectoryName, prefix, numberDigits, subdirectoryManifestDescription, checkIfAllowed );
    }
    

    public <T> OutputSequenceNonIncrementalChecked<T> createNonIncremental(
            Generator<T> generator, Outputter outputter) {

        return new OutputSequenceNonIncrementalChecked<>(
                outputter.getChecked(),
                Optional.of(subdirectoryName),
                outputNameStyle(),
                generator,
                checkIfAllowed,
                subdirectoryManifestDescription);
    }
    
    /**
     * Generates a file for each element in a stream in a sub-directory with an increasing integer sequence in the file-name.
     * 
     * @param <T> element-type in stream
     * @param stream the items to generate separate files
     * @param generator the generator to use for creating each file
     * @param context where writing occurs to
     */
    public <T> void writeStreamAsSubdirectory(
            Stream<T> stream,
            Generator<T> generator,
            InputOutputContext context) {

        OutputSequenceIncremental<T> sequenceWriter = createIncremental(generator,context);

        sequenceWriter.start();
        try {
            stream.forEach(sequenceWriter::add);
        } finally {
            sequenceWriter.end();
        }
    }

    public <T> OutputSequenceIncremental<T> createIncremental(
            Generator<T> generator, InputOutputContext context) {

        return new OutputSequenceIncremental<>(
                new OutputSequenceIncrementalChecked<>(
                        context.getOutputter().getChecked(),
                        subdirectoryName,
                        outputNameStyle(),
                        generator,
                        0,
                        checkIfAllowed,
                        subdirectoryManifestDescription),
                context.getErrorReporter());
    }
    
    public IntegerSuffixOutputNameStyle outputNameStyle() {
        return new IntegerSuffixOutputNameStyle(prefix, numberDigits);
    }
}
