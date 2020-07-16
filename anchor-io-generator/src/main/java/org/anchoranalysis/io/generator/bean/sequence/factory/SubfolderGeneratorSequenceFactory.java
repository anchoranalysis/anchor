/* (C)2020 */
package org.anchoranalysis.io.generator.bean.sequence.factory;

import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceNonIncremental;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceNonIncrementalWriter;
import org.anchoranalysis.io.namestyle.IntegerSuffixOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

public class SubfolderGeneratorSequenceFactory extends GeneratorSequenceFactory {

    @Override
    public <T> GeneratorSequenceNonIncremental<T> createGeneratorSequenceNonIncremental(
            BoundOutputManagerRouteErrors outputManager,
            String outputName,
            IterableObjectGenerator<T, Stack> generator) {

        return new GeneratorSequenceNonIncrementalWriter<>(
                outputManager.getDelegate(),
                outputName,
                new IntegerSuffixOutputNameStyle(outputName, 6),
                generator,
                true);
    }
}
