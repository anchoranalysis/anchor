package org.anchoranalysis.io.generator;

import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor
public abstract class OneStageGeneratorWithElement<T> extends OneStageGenerator<T> {

    private T element;
        
    @Override
    public T getIterableElement() {
        return element;
    }

    @Override
    public void setIterableElement(T element) {
        this.element = element;
    }
    
    @Override
    public OneStageGenerator<T> getGenerator() {
        return this;
    }

    @Override
    public T transform() throws OutputWriteFailedException {
        // Nothing to be done, as there is no preprocessing required
        return element;
    }
}
