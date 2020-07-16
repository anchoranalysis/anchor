/* (C)2020 */
package org.anchoranalysis.experiment.log;

import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;

public class ConsoleMessageLogger implements StatefulMessageLogger {

    @Override
    public void start() {
        // NOTHING TO DO
    }

    @Override
    public void log(String message) {
        System.out.println(message); // NOSONAR
    }

    @Override
    public void logFormatted(String formatString, Object... args) {
        log(String.format(formatString, args));
    }

    @Override
    public void close(boolean successful) {
        // NOTHING TO CLOSE
    }
}
