/*-
 * #%L
 * anchor-test
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

package org.anchoranalysis.test;

import static org.mockito.Mockito.mock;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.core.time.OperationContext;

/**
 * Fixture to create a {@link Logger} that don't output anything.
 *
 * <p>A {@link OperationContext} containg such a logger may also be created.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoggerFixture {

    /**
     * A {@link MessageLogger} that doesn't output anything.
     *
     * @return the message-logger.
     */
    public static MessageLogger suppressedMessageLogger() {
        return mock(MessageLogger.class);
    }

    /**
     * A {@link Logger} that doesn't output anything.
     *
     * @return the logger.
     */
    public static Logger suppressedLogger() {
        return new Logger(suppressedMessageLogger());
    }

    /**
     * An {@link OperationContext} containing a logger that doesn't output anything.
     *
     * @return the operation-context.
     */
    public static OperationContext suppressedOperationContext() {
        return new OperationContext(suppressedLogger());
    }
}
