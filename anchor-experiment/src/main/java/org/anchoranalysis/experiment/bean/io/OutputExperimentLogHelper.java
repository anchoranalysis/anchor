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

package org.anchoranalysis.experiment.bean.io;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.experiment.log.Divider;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.io.output.writer.RecordedOutputs;
import org.apache.commons.lang.time.StopWatch;

/**
 * Helps creating and outputting messages to the log for {@link
 * org.anchoranalysis.experiment.bean.io.OutputExperiment}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class OutputExperimentLogHelper {

    private static final Divider DIVIDER = new Divider();

    public static void maybeLogStart(ParametersExperiment params) {
        if (params.isDetailedLogging()) {
            params.getLoggerExperiment()
                    .logFormatted(
                            "Experiment %s started writing to %s",
                            params.getExperimentIdentifier(),
                            params.getOutputter().getOutputFolderPath());
        }
    }

    public static void maybeLogCompleted(
            RecordedOutputs recordedOutputs,
            ParametersExperiment params,
            StopWatch stopWatchExperiment) {
        if (params.isDetailedLogging()) {

            params.getLoggerExperiment()
                    .logFormatted(
                            "%s%n%s%n%s%nExperiment %s completed (%ds) writing to %s",
                            DIVIDER.withLabel("Outputs"),
                            summarize(recordedOutputs),
                            DIVIDER.withoutLabel(),
                            params.getExperimentIdentifier(),
                            stopWatchExperiment.getTime() / 1000,
                            params.getOutputter().getOutputFolderPath());
        }
    }

    /**
     * A string (one or two lines) summarizing what outputs were written or not-written but
     * possible.
     *
     * @return the string
     */
    private static String summarize(RecordedOutputs recordedOutputs) {
        if (recordedOutputs.isEmpty()) {
            return "No outputs were written as no possible outputs exist.";
        }

        if (recordedOutputs.numberEnabled() > 0) {
            if (recordedOutputs.numberNotAllowed() > 0) {
                return String.format(
                        "Written:\t%s.%nNot written:\t%s.",
                        recordedOutputs.summarizeEnabled(), recordedOutputs.summarizeDisabled());
            } else {
                return String.format(
                        "All possible outputs were written: %s.",
                        recordedOutputs.summarizeEnabled());
            }
        } else {
            return String.format(
                    "No outputs were written.%nPossible outputs are: %s.",
                    recordedOutputs.summarizeDisabled());
        }
    }
}
