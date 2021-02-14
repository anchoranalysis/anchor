package org.anchoranalysis.core.log.error;

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

/**
 * Reports errors as they occur.
 * 
 * <p>A lower category of "error", styled a warning that can be reported.
 * 
 * @author Owen Feehan
 *
 */
public interface ErrorReporter {

    /**
     * Reports an error that occurred as an exception
     *
     * @param classOriginating a class in which the error originates (where it was thrown)
     * @param exc the error that occurred
     */
    void recordError(Class<?> classOriginating, Throwable exc);

    /**
     * Reports an <b>error</b> with a constant string error message.
     *
     * @param classOriginating a class in which the error originates (where it was thrown)
     * @param message a message describing the error that occurred
     */
    void recordError(Class<?> classOriginating, String message);
    
    /**
     * Reports an <b>warning</b> with a constant string error message.
     *
     * @param message a message describing the warning.
     */
    void recordWarning(String message);
    
    /**
     * Checks if at least one warning has been outputted.
     * 
     * @return true if at least one warning has occurred, false otherwise.
     */
    boolean hasWarningOccurred();
        
    /**
     * Reports an <b>error</b> with a string derived from a format-string.
     *
     * @param classOriginating a class in which the error originates (where it was thrown)
     * @param format the format-string for the error message
     * @param args the other arguments for the format-string like in {@link String#format}.
     */
    default void recordErrorFormatted(Class<?> classOriginating, String format, Object...args) {
        recordError( classOriginating, String.format(format, args) );
    }
    
    /**
     * Reports an <b>warning</b> with a string derived from a format-string.
     *
     * @param format the format-string for the warning message
     * @param args the other arguments for the format-string like in {@link String#format}.
     */
    default void recordWarningFormatted(String format, Object...args) {
        recordWarning(String.format(format, args) );
    }
}
