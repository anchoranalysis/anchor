/*-
 * #%L
 * anchor-core
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

package org.anchoranalysis.core.identifier.provider;

import org.anchoranalysis.core.exception.combinable.AnchorCombinableException;
import org.anchoranalysis.core.index.GetOperationFailedException;

/**
 * When a <i>get</i> operation fails with a {@link NamedProvider}.
 *
 * @author Owen Feehan
 */
public class NamedProviderGetException extends AnchorCombinableException {

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Create with a particular cause.
     *
     * @param key the key used for the get query, which caused this exception to be thrown.
     * @param cause the cause
     */
    public NamedProviderGetException(String key, Throwable cause) {
        super(key, cause);
    }

    /**
     * Creates using a standard-error message for a <i>non-existing item</i>
     *
     * @param key the key used for the get query, which caused this exception to be thrown.
     * @return a newly created exception, with a {@link GetOperationFailedException} as cause.
     */
    public static NamedProviderGetException nonExistingItem(String key) {
        return new NamedProviderGetException(
                key,
                new GetOperationFailedException(
                        key, String.format("The item '%s' doesn't exist!", key)));
    }

    /**
     * Like {@link #nonExistingItem(String)} but with a message that also includes a
     * user-friendly-name for the {@link NamedProvider}.
     *
     * @param key the key used for the get query, which caused this exception to be thrown.
     * @param providerName user-friendly name for the {@link NamedProvider} that caused this
     *     exception to be thrown.
     * @return a newly created exception, with a {@link GetOperationFailedException} as cause.
     */
    public static NamedProviderGetException nonExistingItem(String key, String providerName) {
        return new NamedProviderGetException(
                key,
                new GetOperationFailedException(
                        key,
                        String.format("The item '%s' doesn't exist in '%s'!", key, providerName)));
    }

    @Override
    protected boolean canExceptionBeCombined(Throwable exception) {
        return exception instanceof NamedProviderGetException;
    }

    @Override
    protected boolean canExceptionBeSkipped(Throwable exception) {
        return !(exception instanceof NamedProviderGetException);
    }

    @Override
    public Throwable summarize() {
        return super.combineDescriptionsRecursively("at ", "\n-> ");
    }

    @Override
    protected String createMessageForDescription(String description) {
        return description;
    }
}
