/* (C)2020 */
package org.anchoranalysis.io.bean.input;

import java.util.List;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.input.InputFromManager;

/**
 * Base class for describing the input to an experiment (i.e. which files etc. form the source of
 * the experimenent)
 *
 * @author Owen Feehan
 * @param <T> input-type
 */
public abstract class InputManager<T extends InputFromManager> extends AnchorBean<InputManager<T>> {

    public abstract List<T> inputObjects(InputManagerParams params) throws AnchorIOException;
}
