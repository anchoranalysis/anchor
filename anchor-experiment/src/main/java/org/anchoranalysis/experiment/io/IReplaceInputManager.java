/* (C)2020 */
package org.anchoranalysis.experiment.io;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.bean.input.InputManager;

/**
 * If an experiment implements this interface, the input-manager of an an experiment can be replaced
 * by another
 *
 * @author Owen Feehan
 */
public interface IReplaceInputManager {

    public void replaceInputManager(InputManager<?> inputManager) throws OperationFailedException;
}
