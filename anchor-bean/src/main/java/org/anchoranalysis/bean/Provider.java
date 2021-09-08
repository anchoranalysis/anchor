/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.bean;

import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.core.functional.checked.CheckedSupplier;

/**
 * A class (usually an {@link AnchorBean}} that creates or otherwise supplies another object.
 *
 * <p>This is like a {@link CheckedSupplier} but throws a particular type of exception.
 *
 * <p>It is a convenient base-class for a set of beans that provide similar functionality creating
 * or referencing existing objects.
 *
 * @author Owen Feehan
 * @param <T> the item the bean creates
 */
@FunctionalInterface
public interface Provider<T> {

    /**
     * Gets or creates an object of type {@code T}.
     *
     * @return the object returned by the provider.
     * @throws ProvisionFailedException if the object cannot be returned.
     */
    T get() throws ProvisionFailedException;
}