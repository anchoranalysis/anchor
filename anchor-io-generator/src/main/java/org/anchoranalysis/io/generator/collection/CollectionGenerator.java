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
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.sequence.OutputSequenceFactory;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPatternIntegerSuffix;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Writes a collection of elements as a subdirectory with each element as a single file in the subdirectory.
 * 
 * @author Owen Feehan
 * @param <T> element-type in collection
 * @param <S> collection-type
 */
@RequiredArgsConstructor @AllArgsConstructor
public class CollectionGenerator<T, S extends Collection<T>> implements Generator<S> {

    // START REQUIRED ARGUMENTS
    /** Generator to use for writing each element. */
    private final Generator<T> generator;
    
    /** Prefix in outputted name for each element. */
    private final String prefix;
    // END REQUIRED ARGUMENTS

    private S element;

    @Override
    public void write(OutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException {

        writeElementAsSubdirectory(
                outputter, outputNameStyle.getFilenameWithoutExtension());
    }

    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle, String index, OutputterChecked outputter)
            throws OutputWriteFailedException {
        write(outputNameStyle, outputter);
        return 1;
    }

    @Override
    public Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings)
            throws OperationFailedException {
        return generator.getFileTypes(outputWriteSettings);
    }

    @Override
    public S getElement() {
        return element;
    }

    @Override
    public void assignElement(S element) throws SetOperationFailedException {
        this.element = element;
    }
    
    private void writeElementAsSubdirectory(
            OutputterChecked outputter,
            String outputNameFolder
            )
            throws OutputWriteFailedException {
        
        OutputSequenceFactory<T> factory = new OutputSequenceFactory<>(generator,outputter);
        
        OutputPatternIntegerSuffix pattern = new OutputPatternIntegerSuffix(
            outputNameFolder,
            prefix,
            3,
            false,
            Optional.empty()        
        );
        factory.incrementingByOneStream(pattern, element.stream());
    }
    
    public static ManifestDescription createManifestDescription(String type) {
        return new ManifestDescription("subfolder", type);
    }
}
