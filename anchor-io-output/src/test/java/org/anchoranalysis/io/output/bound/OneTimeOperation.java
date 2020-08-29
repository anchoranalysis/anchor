package org.anchoranalysis.io.output.bound;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.io.output.writer.WriterExecuteBeforeEveryOperation;
import lombok.Getter;

/**
 * An operation has been called only once, or else is throws an exception if called again.
 * 
 * @author Owen Feehan
 *
 */
class OneTimeOperation implements WriterExecuteBeforeEveryOperation {

    @Getter private boolean called = false;
    
    @Override
    public void execute() {
        if (called==false) {
            this.called = true;
        } else {
            throw new AnchorFriendlyRuntimeException("execute() has already been called");
        }
    }
}