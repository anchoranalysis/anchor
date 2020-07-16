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
