package org.anchoranalysis.experiment.bean.processor;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.OptionalFactory;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.experiment.task.ParametersExperiment;

/**
 * Utility functions used across multiple processors.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ProcessorUtilities {

    /**
     * A logger that can be used with a job-monitor, or not, if no suitable logger exists.
     *
     * @param parameters parameters from which to find a suitable logger.
     * @return the logger if a suitable settings have been activated, {@link Optional#empty()}
     *     otherwise.
     */
    public static Optional<MessageLogger> loggerForMonitor(ParametersExperiment parameters) {
        return OptionalFactory.create(
                parameters.isDetailedLogging(), parameters::getLoggerExperiment);
    }
}
