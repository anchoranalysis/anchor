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
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.StoreSupplier;
import org.anchoranalysis.core.log.Logger;

/**
 * A sub-item of a {@link MultiInput} that manages a map of objects.
 *
 * <p>This interface provides methods to:
 * <ol>
 *   <li>Add entries to the map.</li>
 *   <li>Copy the contents of the map into a {@link NamedProviderStore}.</li>
 *   <li>Retrieve entries from the map.</li>
 * </ol>
 *
 * @param <T> the type of objects stored in the map
 */
public interface MultiInputSubMap<T> {

    /**
     * Adds an entry to the map.
     *
     * @param name the name of the entry
     * @param supplier the supplier for the entry's value
     */
    void add(String name, StoreSupplier<T> supplier);

    /**
     * Copies all the existing entries into a {@link NamedProviderStore}.
     *
     * @param namedStore the store to copy the entries into
     * @param logger a logger for any non-fatal errors
     * @throws OperationFailedException if a fatal error occurs during the copy process
     */
    void addToStore(NamedProviderStore<T> namedStore, Logger logger)
            throws OperationFailedException;

    /**
     * Retrieves an entry from the map.
     *
     * @param name the name of the entry to retrieve
     * @return the {@link StoreSupplier} for the entry, or null if the entry doesn't exist
     * @throws OperationFailedException if an error occurs while retrieving the entry
     */
    StoreSupplier<T> get(String name) throws OperationFailedException;
}