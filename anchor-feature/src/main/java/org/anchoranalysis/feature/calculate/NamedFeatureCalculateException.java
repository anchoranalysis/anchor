/*-
 * #%L
 * anchor-feature
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
package org.anchoranalysis.feature.calculate;

import org.anchoranalysis.core.exception.friendly.AnchorFriendlyCheckedException;

/**
 * An exception that occurs when a feature fails to successfully calculate.
 *
 * <p>The feature may optionally have a name associated with it, to help identify it to the user.
 *
 * @author Owen Feehan
 */
public class NamedFeatureCalculateException extends AnchorFriendlyCheckedException {

    private static final long serialVersionUID = 1L;

    /** The message without any key identifier. */
    private final String messageWithoutKey;

    /**
     * Creates with a general failure message that doesn't pertain to any particular feature.
     *
     * @param message the message.
     */
    public NamedFeatureCalculateException(String message) {
        super(message);
        this.messageWithoutKey = message;
    }

    /**
     * Creates with a calculation error that doesn't pertain to any particular feature.
     *
     * @param cause reason for failure.
     */
    public NamedFeatureCalculateException(Exception cause) {
        this(cause.toString());
    }

    /**
     * Creates with a failure message associated with a particular feature.
     *
     * @param featureName a name to describe the feature whose calculation failed.
     * @param message the reason for failure.
     */
    public NamedFeatureCalculateException(String featureName, String message) {
        super(
                String.format(
                        "An error occurred when calculating feature '%s': %s",
                        featureName, message));
        messageWithoutKey = message;
    }

    /**
     * Removes any feature-name from the exception.
     *
     * @return a newly-created similar exception, but lacking any identifier for which feature
     *     caused the exception.
     */
    public FeatureCalculationException dropKey() {
        return new FeatureCalculationException(messageWithoutKey);
    }
}
