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
package org.anchoranalysis.feature.calc;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

/**
 * A feature-calculation exception that occurs in a particular named feature, or else a general
 * message of failure.
 *
 * @author Owen Feehan
 */
public class NamedFeatureCalculationException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1L;

    /** The message without any key identifier */
    private final String messageWithoutKey;

    /**
     * Constructor - when a general failure message that doesn't pertain to any particular feature.
     *
     * @param message
     */
    public NamedFeatureCalculationException(String message) {
        super(message);
        this.messageWithoutKey = message;
    }

    /**
     * Constructor - when a calculation error occurs associated with the only pertinent feature.
     *
     * @param message
     */
    public NamedFeatureCalculationException(Exception exception) {
        this(exception.toString());
    }

    /**
     * Constructor - when a particular named feature failed in some way
     *
     * @param featureName a name to describe the feature whose calculation failed
     * @param message the reason for failure
     */
    public NamedFeatureCalculationException(String featureName, String message) {
        super(
                String.format(
                        "An error occurred when calculating feature '%s': %s",
                        featureName, message));
        messageWithoutKey = message;
    }

    /** Ignores the name in the exception */
    public FeatureCalculationException dropKey() {
        return new FeatureCalculationException(messageWithoutKey);
    }
}
