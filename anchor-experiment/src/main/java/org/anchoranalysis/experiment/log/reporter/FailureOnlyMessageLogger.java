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
/* (C)2020 */
package org.anchoranalysis.experiment.log.reporter;

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

/**
 * Writes text to a file, but only if close is called with a successful=true
 *
 * <p>The text cannot be written immediately, so is saved until close() is called.
 *
 * @author feehano
 */
@RequiredArgsConstructor
public class FailureOnlyMessageLogger implements StatefulMessageLogger {

    // START REQUIRED ARGUMENTS
    private final String outputName;
    private final BoundOutputManager outputManager;
    private final ErrorReporter errorReporter;
    // END REQUIRED ARGUMENTS

    private StringBuilder sb;

    @Override
    public void log(String message) {
        sb.append(message);
        sb.append(System.lineSeparator());
    }

    @Override
    public void logFormatted(String formatString, Object... args) {
        log(String.format(formatString, args));
    }

    @Override
    public void start() {
        sb = new StringBuilder();
    }

    @Override
    public void close(boolean successful) {
        if (!successful) {
            writeStringToFile(sb.toString());
        }
    }

    private void writeStringToFile(String message) {

        try {
            OptionalUtilities.ifPresent(
                    TextFileLogHelper.createOutput(outputManager, outputName),
                    output -> {
                        output.start();
                        output.getWriter().append(message);
                        output.end();
                    });

        } catch (AnchorIOException e) {
            errorReporter.recordError(MessageLogger.class, e);
        }
    }
}
