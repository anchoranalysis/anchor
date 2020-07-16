/* (C)2020 */
package org.anchoranalysis.io.generator.sequence;

import java.util.Collection;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class GeneratorSequenceIncrementalCollection<T, C>
        implements GeneratorSequenceIncremental<T> {

    private IterableObjectGenerator<T, C> iterableGenerator;
    private Collection<C> collection;

    public GeneratorSequenceIncrementalCollection(
            Collection<C> collection, IterableObjectGenerator<T, C> iterableGenerator) {
        super();

        this.collection = collection;
        this.iterableGenerator = iterableGenerator;
    }

    @Override
    public void add(T element) throws OutputWriteFailedException {

        try {
            iterableGenerator.setIterableElement(element);

            C generatedElement = iterableGenerator.getGenerator().generate();
            collection.add(generatedElement);
        } catch (SetOperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public void start() throws OutputWriteFailedException {
        iterableGenerator.start();
    }

    @Override
    public void end() throws OutputWriteFailedException {
        iterableGenerator.end();
    }
}
