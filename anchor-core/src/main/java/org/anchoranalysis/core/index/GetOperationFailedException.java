package org.anchoranalysis.core.index;

import lombok.Getter;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyCheckedException;

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
 * When a <i>get</i> operation fails for a particular key.
 *
 * @author Owen Feehan
 */
public class GetOperationFailedException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 4847382915351464834L;

    /** The key that was used in the operation that created this exception. */
    @Getter private final String key;

    /** A message describing the failure. */
    @Getter private final String message;

    /**
     * Creates with only a cause, omitting a key and a message.
     *
     * <p>The key is instead stored as an empty string.
     *
     * @param cause the cause.
     */
    public GetOperationFailedException(Exception cause) {
        super(cause);
        this.key = "";
        this.message = cause.getMessage();
    }

    /**
     * Creates with a message, but no cause - with <i>an integer key</i>.
     *
     * @param key the key that was used in the operation that created this exception.
     * @param message a message describing the failure.
     */
    public GetOperationFailedException(int key, String message) {
        this(String.valueOf(key), message);
    }

    /**
     * Creates with a message, but no cause - with <i>a {@link String} key</i>.
     *
     * @param key the key that was used in the operation that created this exception.
     * @param message a message describing the failure.
     */
    public GetOperationFailedException(String key, String message) {
        super(messageFor(key, message));
        this.key = key;
        this.message = message;
    }

    /**
     * Creates with a cause, but no message - with <i>an integer key</i>.
     *
     * @param key the key that was used in the operation that created this exception.
     * @param cause the cause of the failure.
     */
    public GetOperationFailedException(int key, Throwable cause) {
        this(String.valueOf(key), cause);
    }

    /**
     * Creates with a cause, but no message - with <i>a {@link String} key</i>.
     *
     * @param key the key that was used in the operation that created this exception.
     * @param cause the cause of the failure.
     */
    public GetOperationFailedException(String key, Throwable cause) {
        super(messageFor(key, cause.toString()), cause);
        this.key = key;
        this.message = cause.toString();
    }

    /**
     * Converts to a {@link OperationFailedException} using an identical message.
     *
     * <p>Any cause and key information is discarded.
     *
     * @return the converted exception.
     */
    public OperationFailedException asOperationFailedException() {
        return new OperationFailedException(getMessage());
    }

    private static String messageFor(String key, String message) {
        return String.format("An exception occurred getting '%s':%n%s", key, message);
    }
}
