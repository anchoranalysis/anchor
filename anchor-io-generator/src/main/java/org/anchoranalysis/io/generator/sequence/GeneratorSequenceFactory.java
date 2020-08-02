package org.anchoranalysis.io.generator.sequence;

import lombok.AllArgsConstructor;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.namestyle.IntegerSuffixOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

@AllArgsConstructor
public class GeneratorSequenceFactory {

    private static final boolean CHECK_IF_ALLOWED = false;

    /** Name of sub-directory to place sequence in (which is also used as the outputName) */
    private final String subdirectoryName;

    /**
     * Name of the prefix of each file in the sub-directory.
     *
     * <p>The full-name has an increment number appended e.g. <code>$prefix_000000.tif</code> etc.
     */
    private final String filePrefixName;

    public <T> GeneratorSequenceIncrementalRerouteErrors<T> createIncremental(
            IterableGenerator<T> generator, BoundIOContext context) {

        return new GeneratorSequenceIncrementalRerouteErrors<>(
                new GeneratorSequenceIncrementalWriter<>(
                        context.getOutputManager().getDelegate(),
                        subdirectoryName,
                        outputNameStyle(filePrefixName),
                        generator,
                        0,
                        CHECK_IF_ALLOWED),
                context.getErrorReporter());
    }

    public <T> GeneratorSequenceNonIncremental<T> createNonIncremental(
            IterableGenerator<T> generator, BoundOutputManagerRouteErrors outputManager) {

        return new GeneratorSequenceNonIncrementalWriter<>(
                outputManager.getDelegate(),
                subdirectoryName,
                outputNameStyle(filePrefixName),
                generator,
                CHECK_IF_ALLOWED);
    }

    private static IntegerSuffixOutputNameStyle outputNameStyle(String outputName) {
        return new IntegerSuffixOutputNameStyle(outputName, 6);
    }
}
