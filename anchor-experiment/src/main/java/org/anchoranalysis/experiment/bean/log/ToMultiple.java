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

package org.anchoranalysis.experiment.bean.log;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.anchoranalysis.experiment.log.MultipleLoggers;
import org.anchoranalysis.experiment.log.StatefulMessageLogger;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Rather than logging to one location, logs to multiple locations (from a list).
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class ToMultiple extends LoggingDestination {

    // START BEAN
    /** The list of loggers to log to */
    @BeanField @Getter @Setter private List<LoggingDestination> list = Arrays.asList();
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
            OutputterChecked outputter,
            ErrorReporter errorReporter,
            ExecutionArguments arguments,
            boolean detailedLogging) {
        return new MultipleLoggers(
                list.stream()
                        .map(
                                logger ->
                                        logger.create(
                                                outputter,
                                                errorReporter,
                                                arguments,
                                                detailedLogging)));
    }
}
