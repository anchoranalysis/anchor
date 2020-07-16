/* (C)2020 */
package org.anchoranalysis.experiment.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

/**
 * Input for executing a task, associated with shared-state and other parameters.
 *
 * @author Owen Feehan
 * @param <T> input-object type
 * @aram <S> shared-state type
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

    public BoundOutputManagerRouteErrors getOutputManager() {
        return context.getOutputManager();
    }

    public Logger getLogger() {
        return context.getLogger();
    }

    public StatefulMessageLogger getLogReporterJob() {
        return context.getStatefulLogReporter();
    }
}
