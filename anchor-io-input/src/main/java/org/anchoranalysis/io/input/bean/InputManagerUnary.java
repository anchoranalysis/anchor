package org.anchoranalysis.io.input.bean;

import java.util.List;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.input.InputReadFailedException;
import lombok.Getter;
import lombok.Setter;

/**
 * Base class for an {@link InputManager} that delegates to another {@link InputManager}
 * with the same input-type.
 * 
 * @author Owen Feehan
 *
 * @param <T> input-type
 */
public abstract class InputManagerUnary<T extends InputFromManager> extends InputManager<T> {
    
    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private InputManager<T> input;
    // END BEAN PROPERITES
    
    @Override
    public List<T> inputs(InputManagerParams params) throws InputReadFailedException {
        return inputsFromDelegate( input.inputs(params), params );
    }
    
    /**
     * Calculates the inputs to return given the inputs from the delegate.
     *
     * @param fromDelegate the inputs from the delegate
     * @param params parameters for determining inputs
     * @return inputs to return after any further processing
     */
    protected abstract List<T> inputsFromDelegate(List<T> fromDelegate, InputManagerParams params) throws InputReadFailedException;
}
