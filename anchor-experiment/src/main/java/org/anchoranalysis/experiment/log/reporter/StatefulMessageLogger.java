/* (C)2020 */
package org.anchoranalysis.experiment.log.reporter;

import org.anchoranalysis.core.log.MessageLogger;

/** A {@link MessageLogger} that can be started and stopped, and is aware this state. */
public interface StatefulMessageLogger extends MessageLogger {

    void start();

    void close(boolean successful);
}
