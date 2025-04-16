/*-
 * #%L
 * anchor-mpp-io
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

package org.anchoranalysis.mpp.io.input;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.log.Logger;

/**
 * An interface for exporting shared objects to a target container.
 *
 * <p>This interface defines a method for copying certain shared objects to a target
 * {@link SharedObjects} container. Implementations of this interface should define
 * the specific logic for exporting their shared objects.</p>
 */
public interface ExportSharedObjects {

    /**
     * Adds any exported shared objects to the target container.
     *
     * <p>This method is responsible for copying the relevant shared objects from the
     * implementing class to the provided target container. Non-fatal errors should be
     * logged using the provided logger, while fatal errors should throw an
     * {@link OperationFailedException}.</p>
     *
     * @param target the {@link SharedObjects} container where the exported objects will be added
     * @param logger a {@link Logger} for reporting non-fatal errors
     * @throws OperationFailedException if a fatal error occurs during the export process
     */
    void copyTo(SharedObjects target, Logger logger) throws OperationFailedException;
}