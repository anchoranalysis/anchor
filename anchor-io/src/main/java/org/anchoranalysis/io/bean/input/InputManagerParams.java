/* (C)2020 */
package org.anchoranalysis.io.bean.input;

import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.io.params.DebugModeParams;
import org.anchoranalysis.io.params.InputContextParams;

/**
 * Parameters passed to an InputManager to generate input-objects
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class InputManagerParams {

    @Getter private final InputContextParams inputContext;

    @Getter private final ProgressReporter progressReporter;

    @Getter private final Logger logger;

    public InputManagerParams withProgressReporter(ProgressReporter progressReporterToAssign) {
        return new InputManagerParams(inputContext, progressReporterToAssign, logger);
    }

    public boolean isDebugModeActivated() {
        return inputContext.getDebugModeParams().isPresent();
    }

    public Optional<DebugModeParams> getDebugModeParams() {
        return inputContext.getDebugModeParams();
    }
}
