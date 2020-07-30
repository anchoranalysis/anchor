package org.anchoranalysis.io.generator.sequence;

import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.namestyle.IntegerSuffixOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class GeneratorSequenceFactory {

    public static <T> GeneratorSequenceIncrementalRerouteErrors<T> createIncremental(
            String subdirectoryName, String outputName, IterableGenerator<T> generator, BoundIOContext context) {

        return new GeneratorSequenceIncrementalRerouteErrors<>(
                new GeneratorSequenceIncrementalWriter<>(
                        context.getOutputManager().getDelegate(),
                        subdirectoryName,
                        outputNameStyle(outputName),
                        generator,
                        0,
                        true),
                context.getErrorReporter());
    }
    
    public static <T> GeneratorSequenceNonIncremental<T> createNonIncremental(
            String outputName,
            IterableGenerator<T> generator,
            BoundOutputManagerRouteErrors outputManager) {

        return new GeneratorSequenceNonIncrementalWriter<>(
                outputManager.getDelegate(),
                outputName,
                outputNameStyle(outputName),
                generator,
                true);
    }
    
    private static IntegerSuffixOutputNameStyle outputNameStyle(String outputName) {
        return new IntegerSuffixOutputNameStyle(outputName, 6);
    }
}
