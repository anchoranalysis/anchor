/* (C)2020 */
package org.anchoranalysis.experiment.bean.log;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.log.MessageLoggerList;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

/**
 * Rather than logging to one location, logs to multiple locations (from a list).
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class ToMultiple extends LoggingDestination {

    // START BEAN
    /** The list of loggers to log to */
    @BeanField @Getter @Setter private List<LoggingDestination> list = new ArrayList<>();
    // END BEAN

    /**
     * Constructs a logger to two locations
     *
     * @param first first-location
     * @param second second-location
     */
    public ToMultiple(LoggingDestination first, LoggingDestination second) {
        this();
        list.add(first);
        list.add(second);
    }

    @Override
    public StatefulMessageLogger create(
            BoundOutputManager outputManager,
            ErrorReporter errorReporter,
            ExperimentExecutionArguments arguments,
            boolean detailedLogging) {
        return new MessageLoggerList(
                list.stream()
                        .map(
                                logger ->
                                        logger.create(
                                                outputManager,
                                                errorReporter,
                                                arguments,
                                                detailedLogging)));
    }
}
