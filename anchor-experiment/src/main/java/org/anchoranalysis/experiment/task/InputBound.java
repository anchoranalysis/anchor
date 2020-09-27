/*-
 * #%L
 * anchor-experiment
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

package org.anchoranalysis.experiment.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.experiment.log.StatefulMessageLogger;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.Outputter;

/**
 * Input for executing a task, associated with shared-state and other parameters.
 *
 * @author Owen Feehan
 * @param <T> input-object type
 * @param <S> shared-state type
 */
@AllArgsConstructor
public class InputBound<T, S> {

    @Getter private final T inputObject;

    @Getter private final S sharedState;

    @Getter private final ManifestRecorder manifest;

    @Getter private final boolean detailedLogging;

    private final BoundContextSpecify context;

    /** Immutably changes the input-object */
    public <U> InputBound<U, S> changeInputObject(U inputObjectNew) {
        return new InputBound<>(inputObjectNew, sharedState, manifest, detailedLogging, context);
    }

    public BoundIOContext context() {
        return context;
    }

    public Outputter getOutputter() {
        return context.getOutputter();
    }

    public Logger getLogger() {
        return context.getLogger();
    }

    public StatefulMessageLogger getLogReporterJob() {
        return context.getStatefulLogReporter();
    }
}
