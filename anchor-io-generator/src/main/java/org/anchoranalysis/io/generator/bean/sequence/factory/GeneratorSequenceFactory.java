/* (C)2020 */
package org.anchoranalysis.io.generator.bean.sequence.factory;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceNonIncremental;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

public abstract class GeneratorSequenceFactory extends AnchorBean<GeneratorSequenceFactory> {

    /**
     * @param <GeneratorType> generator-type
     * @param outputManager
     * @param outputName
     * @param generator
     * @return
     */
    public abstract <T> GeneratorSequenceNonIncremental<T> createGeneratorSequenceNonIncremental(
            BoundOutputManagerRouteErrors outputManager,
            String outputName,
            IterableObjectGenerator<T, Stack> generator);
}
