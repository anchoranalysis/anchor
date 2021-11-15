/*-
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.io.input.bean;

import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.core.progress.ProgressIgnore;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.core.time.ExecutionTimeRecorderIgnore;
import org.anchoranalysis.io.input.InputContextParams;

/**
 * Parameters passed to an {@link InputManager} to generate input-objects
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class InputManagerParams {

    @Getter private final InputContextParams inputContext;

    @Getter private final Progress progress;

    /** Allows for recording the execution-time of particular operations. */
    @Getter private final ExecutionTimeRecorder executionTimeRecorder;

    @Getter private final Logger logger;

    /**
     * Create with only a logger, and using sensible default values for the other fields.
     *
     * @param logger the logger.
     */
    public InputManagerParams(Logger logger) {
        this(
                new InputContextParams(),
                ProgressIgnore.get(),
                new ExecutionTimeRecorderIgnore(),
                logger);
    }

    public InputManagerParams withProgressReporter(Progress progressToAssign) {
        return new InputManagerParams(
                inputContext, progressToAssign, executionTimeRecorder, logger);
    }

    public boolean isDebugModeActivated() {
        return inputContext.getDebugModeParams().isPresent();
    }

    public Optional<DebugModeParams> getDebugModeParams() {
        return inputContext.getDebugModeParams();
    }
}
