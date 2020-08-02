/*-
 * #%L
 * anchor-io-manifest
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

package org.anchoranalysis.io.manifest.deserializer.folder;

import java.io.Serializable;
import org.anchoranalysis.core.index.GetterFromIndex;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.folder.FolderWrite;

/**
 * @author Owen Feehan
 * @param <T> history-type
 * @param <S> cache-type
 */
public abstract class DeserializeFromFolderBundle<T, S extends Serializable>
        implements HistoryCreator<T> {

    private final BundleDeserializers<S> deserializers;
    private FolderWrite folder;

    private static final int CACHE_SIZE = 5;

    public DeserializeFromFolderBundle(
            final BundleDeserializers<S> deserializers, FolderWrite folder) {
        this.deserializers = deserializers;
        this.folder = folder;
    }

    @Override
    public LoadContainer<T> create() throws DeserializationFailedException {
        DeserializedObjectFromFolderBundle<S> deserializeFromBundle =
                new DeserializedObjectFromFolderBundle<>(folder, deserializers, CACHE_SIZE);

        BoundedIndexContainer<T> boundedContainer =
                new BoundsFromSequenceType<>(
                        createCntr(deserializeFromBundle),
                        deserializeFromBundle.getBundleParameters().getSequenceType());

        LoadContainer<T> history = new LoadContainer<>();
        history.setCntr(boundedContainer);
        history.setExpensiveLoad(expensiveLoad());

        return history;
    }

    protected abstract GetterFromIndex<T> createCntr(
            DeserializedObjectFromFolderBundle<S> deserializeFromBundle);

    private boolean expensiveLoad() {
        int numberOfBundles =
                folder.getManifestFolderDescription().getSequenceType().getNumElements();
        // If we have more bundles than the size of the cache, then we leave adjusting mode off
        // If our cache is big enough for all our bundles then we go into adjusting mode
        return numberOfBundles > CACHE_SIZE;
    }
}
