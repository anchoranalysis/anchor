/* (C)2020 */
package org.anchoranalysis.io.generator.sequence;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class GeneratorSequenceIncrementalRerouteErrors<T> {

    private GeneratorSequenceIncremental<T> delegate;
    private ErrorReporter errorReporter;

    public GeneratorSequenceIncrementalRerouteErrors(
            GeneratorSequenceIncremental<T> delegate, ErrorReporter errorReporter) {
        super();
        this.delegate = delegate;
        this.errorReporter = errorReporter;
    }

    public void start() {
        try {
            delegate.start();
        } catch (OutputWriteFailedException e) {
            routeError(e);
        }
    }

    public void add(T element) {
        try {
            delegate.add(element);
        } catch (OutputWriteFailedException e) {
            routeError(e);
        }
    }

    public void end() {
        try {
            delegate.end();
        } catch (OutputWriteFailedException e) {
            routeError(e);
        }
    }

    private void routeError(Exception e) {
        errorReporter.recordError(GeneratorSequenceIncrementalRerouteErrors.class, e);
    }
}
