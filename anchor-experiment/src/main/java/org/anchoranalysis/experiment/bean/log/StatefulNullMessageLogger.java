/* (C)2020 */
package org.anchoranalysis.experiment.bean.log;

import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;

/**
 * Does nothing (i.e. simply ignores) with any messages logged.
 *
 * <p>This is kept distinct from {@link org.anchoranalysis.core.log.NullMessageLogger} as it also
 * implements the {@link StatefulMessageLogger} interface which exists in a more downstream package
 * as {@link MessageLogger}.
 *
 * @author Owen Feehan
 */
public class StatefulNullMessageLogger implements StatefulMessageLogger {

    @Override
    public void start() {
        // NOTHING TO DO
    }

    @Override
    public void log(String message) {
        // NOTHING TO DO
    }

    @Override
    public void logFormatted(String formatString, Object... args) {
        // NOTHING TO DO
    }

    @Override
    public void close(boolean successful) {
        // NOTHING TO DO
    }
}
