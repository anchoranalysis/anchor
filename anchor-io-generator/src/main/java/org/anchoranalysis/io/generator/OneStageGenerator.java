package org.anchoranalysis.io.generator;

import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor
public abstract class OneStageGenerator<T> extends SingleFileTypeGenerator<T,T> {
    
    private T element;
    
    @Override
    public T getElement() {
        return element;
    }

    /**
     * Sets the current element.
     * 
     * <p>Note the absence of a checked-exception here, unlike in {@link SingleFileTypeGeneratorWithElement}.
     */
    @Override
    public void assignElement(T element) {
        this.element = element;
    }
    
    @Override
    public T transform() throws OutputWriteFailedException {
        // Nothing to be done, as there is no preprocessing required
        return element;
    }
}
