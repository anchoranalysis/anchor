/* (C)2020 */
package org.anchoranalysis.io.generator.sequence;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class GeneratorSequenceNonIncrementalRerouterErrors<T> {

    private GeneratorSequenceNonIncremental<T> delegate;

    private ErrorReporter errorReporter;

    public GeneratorSequenceNonIncrementalRerouterErrors(
            GeneratorSequenceNonIncremental<T> delegate, ErrorReporter errorReporter) {
        super();
        this.delegate = delegate;
        this.errorReporter = errorReporter;
    }

    public void start(SequenceType sequenceType, int totalNumAdd) {
        try {
            delegate.start(sequenceType, totalNumAdd);
        } catch (OutputWriteFailedException e) {
            routeError(e);
        }
    }

    public void add(T element, String index) {
        try {
            delegate.add(element, index);
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

    public void setSuppressSubfolder(boolean suppressSubfolder) {
        delegate.setSuppressSubfolder(suppressSubfolder);
    }
}
