/* (C)2020 */
package org.anchoranalysis.experiment.io;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.output.bean.OutputManager;

/**
 * If an experiment implements this interface, the input-manager of an an experiment can be replaced
 * by another
 *
 * @author Owen Feehan
 */
public interface IReplaceOutputManager {

    public void replaceOutputManager(OutputManager outputManager) throws OperationFailedException;
}
