/*-
 * #%L
 * anchor-inference
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.inference.concurrency;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.core.value.LanguageUtilities;

/**
 * Logs message about GPUs allocated.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class GPUMessageLogger {

    /** The CUDA version expected, as communicated to the user via a log message. */
    private static final String CUDA_VERSION = "11.2";

    /**
     * Write a log message describing the planned number of GPUs and how many were successfully
     * added.
     *
     * <p>The version of CUDA used is included, to help make sure the user has the appropriate
     * version.
     *
     * <p>Exceptionally, no message is written, if zero GPUs were planned.
     *
     * @param numberPlanned the number of GPUs planned.
     * @param numberAllocated the number of GPUs successfully allocated.
     * @param logger the message-logger.
     */
    public static void maybeLog(int numberPlanned, int numberAllocated, MessageLogger logger) {

        if (numberPlanned == 0) {
            // If no GPUs were planned, don't bother logging.
            return;
        }

        if (numberAllocated == numberPlanned) {
            logger.logFormatted("%s.", prefixGPUAllocation(numberPlanned, true));
        } else {
            String prefix = prefixGPUAllocation(numberPlanned, false);
            String suffix = "Using available CPUs instead.";
            if (numberAllocated > 0) {
                logger.logFormatted(
                        "%s, but only %d could be allocated. %s",
                        prefix, numberPlanned, numberAllocated, suffix);
            } else {
                logger.logFormatted(
                        "%s, but %s could not be allocated. %s",
                        prefix,
                        LanguageUtilities.multiplexPlural(numberPlanned, "it", "they"),
                        suffix);
            }
        }
    }

    /** The first part of the log message about GPU allocation. */
    private static String prefixGPUAllocation(int numberPlanned, boolean includeSuccessful) {
        return String.format(
                "%d GPU %s %s %srequested using the ONNX Runtime (CUDA Version %s)",
                numberPlanned,
                LanguageUtilities.pluralizeMaybe(numberPlanned, "processor"),
                LanguageUtilities.multiplexPlural(numberPlanned, "was", "were"),
                includeSuccessful ? "successfully " : "",
                CUDA_VERSION);
    }
}
