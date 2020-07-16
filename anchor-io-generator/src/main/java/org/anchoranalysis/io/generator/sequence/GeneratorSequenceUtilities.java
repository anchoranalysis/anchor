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
/* (C)2020 */
package org.anchoranalysis.io.generator.sequence;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.IntegerSuffixOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneratorSequenceUtilities {

    public static <T> void generateListAsSubfolder(
            String folderName,
            int numDigits,
            Collection<T> items,
            IterableObjectGenerator<T, Stack> generator,
            BoundIOContext context) {
        IndexableOutputNameStyle outputStyle =
                new IntegerSuffixOutputNameStyle(folderName, numDigits);

        GeneratorSequenceIncrementalRerouteErrors<T> sequenceWriter =
                new GeneratorSequenceIncrementalRerouteErrors<>(
                        new GeneratorSequenceIncrementalWriter<>(
                                context.getOutputManager().getDelegate(),
                                outputStyle.getOutputName(),
                                outputStyle,
                                generator,
                                new ManifestDescription("raster", folderName),
                                0,
                                true),
                        context.getErrorReporter());

        sequenceWriter.start();

        for (T item : items) {
            sequenceWriter.add(item);
        }

        sequenceWriter.end();
    }

    public static <T> void generateListAsSubfolderWithException(
            String folderName,
            int numDigits,
            Collection<T> items,
            IterableObjectGenerator<T, Stack> generator,
            BoundOutputManagerRouteErrors outputManager)
            throws OutputWriteFailedException {

        IndexableOutputNameStyle outputStyle =
                new IntegerSuffixOutputNameStyle(folderName, numDigits);

        GeneratorSequenceIncrementalWriter<T> sequenceWriter =
                new GeneratorSequenceIncrementalWriter<>(
                        outputManager.getDelegate(),
                        outputStyle.getOutputName(),
                        outputStyle,
                        generator,
                        new ManifestDescription("raster", folderName),
                        0,
                        true);

        sequenceWriter.start();

        for (T item : items) {
            sequenceWriter.add(item);
        }

        sequenceWriter.end();
    }
}
