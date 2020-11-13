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

package org.anchoranalysis.io.generator.collection;

import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.sequence.OutputSequenceFactory;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPatternIntegerSuffix;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Writes a collection of elements as a subdirectory with each element as a single file in the
 * subdirectory.
 *
 * @author Owen Feehan
 * @param <T> element-type in collection
 */
@RequiredArgsConstructor
public class CollectionGenerator<T> implements Generator<Collection<T>> {

    // START REQUIRED ARGUMENTS
    /** Generator to use for writing each element. */
    private final Generator<T> generator;

    /** Prefix in outputted name for each element. */
    private final String prefix;
    // END REQUIRED ARGUMENTS

    @Override
    public FileType[] write(
            Collection<T> element, OutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException {
        String subdirectoryName = outputNameStyle.filenameWithoutExtension().orElseThrow( () ->
            new OutputWriteFailedException("No name is assigned in the output, so cannot create subdirectory")
        );
        return writeElementAsSubdirectory(element, outputter, subdirectoryName);
    }

    @Override
    public FileType[] writeWithIndex(
            Collection<T> element,
            String index,
            IndexableOutputNameStyle outputNameStyle,
            OutputterChecked outputter)
            throws OutputWriteFailedException {
        return write(element, outputNameStyle, outputter);
    }

    private FileType[] writeElementAsSubdirectory(
            Collection<T> element, OutputterChecked outputter, String outputNameDirectory)
            throws OutputWriteFailedException {

        OutputSequenceFactory<T> factory = new OutputSequenceFactory<>(generator, outputter);

        OutputPatternIntegerSuffix pattern =
                new OutputPatternIntegerSuffix(
                        outputNameDirectory, prefix, 3, false, Optional.empty());
        factory.incrementingByOneStream(pattern, element.stream());

        // Do not report any file-types for the collection written. TODO is this okay?
        return new FileType[] {};
    }

    public static ManifestDescription createManifestDescription(String type) {
        return new ManifestDescription("subfolder", type);
    }
}
