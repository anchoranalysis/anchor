package org.anchoranalysis.core.exception.friendly;

/*
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Utilities for describing an exception (with lots of nested causes) in a user-friendly way
 *
 * @author Owen Feehan
 */
class HelperFriendlyFormatting {

    /** Incrementally prepended to each exception when describing nested-exceptions */
    private static final String PREPEND_EXCEPTION_STRING = "   ";

    private HelperFriendlyFormatting() {
        // Only static access
    }

    /**
     * A friendly-message describing what went wrong, but WITHOUT describing any further causes
     * (nested exceptions)
     *
     * <p>We search for the first non-empty error message
     *
     * @param excDescribe the exception to describe
     * @return a string with user-friendly error message
     */
    public static String friendlyMessageNonHierarchical(Throwable excDescribe) {

        Throwable e = excDescribe;
        while (e != null) {

            if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                return e.getMessage();
            }

            e = e.getCause();
        }
        throw new IllegalStateException("None of the nested exceptions have an associated message");
    }

    /**
     * A friendly-message describing what went wrong, additionally any further causes (nested
     * exceptions)
     *
     * <p>We display an increasingly-indented multiline message with the nested-causes (skipping any
     * exception with an empty message)
     *
     * @param excDescribe the exception to describe
     * @return a string with user-friendly error message
     */
    public static String friendlyMessageHierarchy(Throwable excDescribe) {
        StringWriter writer = new StringWriter();
        try {
            friendlyMessageHierarchical(excDescribe, writer, -1, false);
        } catch (IOException e) {
            assert false;
            throw new AssertionError(
                    "An IO exception occured generating a friendlyMessage with a StringWriter. This shouldn't happen!",
                    e);
        }
        return writer.toString();
    }

    /**
     * We search for the first non-empty error message
     *
     * @param exc the exception to describe
     * @param writer where the friendly-messaged is outputted
     * @param wordWrapLimit max-number of characters of a single-line of the screen (considering
     *     only prefix + line), if it's -1 it is disabled
     * @param showExceptionType adds a rightmost column showing the exception class type
     * @throws IOException if an I/O error occurs with the writer
     */
    public static void friendlyMessageHierarchical(
            Throwable exc, Writer writer, int wordWrapLimit, boolean showExceptionType)
            throws IOException {
        RecursivelyDescribeExceptionStack.apply(
                exc, writer, PREPEND_EXCEPTION_STRING, wordWrapLimit, showExceptionType);
    }
}
