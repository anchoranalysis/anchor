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

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

/**
 * Switches between two log-reporters depending on whether detailed logging is switched on or not
 *
 * @author Owen Feehan
 */
public class SwitchDetailedLogging extends LoggingDestination {

    // START BEAN PROPERTIES
    /** Logger to use when detailed-logging is on */
    @BeanField @Getter @Setter private LoggingDestination whenDetailed;

    /** Logger to use when detailed-logging is off */
    @BeanField @Getter @Setter private LoggingDestination whenNot;
    // END BEAN PROPERTIES

    @Override
    public StatefulMessageLogger create(
            BoundOutputManager bom,
            ErrorReporter errorReporter,
            ExperimentExecutionArguments arguments,
            boolean detailedLogging) {
        if (detailedLogging) {
            return whenDetailed.create(bom, errorReporter, arguments, detailedLogging);
        } else {
            return whenNot.create(bom, errorReporter, arguments, detailedLogging);
        }
    }
}
