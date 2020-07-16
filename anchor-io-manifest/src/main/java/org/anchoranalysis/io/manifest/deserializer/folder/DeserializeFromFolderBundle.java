/* (C)2020 */
package org.anchoranalysis.io.manifest.deserializer.folder;

import java.io.Serializable;
import org.anchoranalysis.core.index.ITypedGetFromIndex;
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

    protected abstract ITypedGetFromIndex<T> createCntr(
            DeserializedObjectFromFolderBundle<S> deserializeFromBundle);

    private boolean expensiveLoad() {
        int numberOfBundles =
                folder.getManifestFolderDescription().getSequenceType().getNumElements();
        // If we have more bundles than the size of the cache, then we leave adjusting mode off
        // If our cache is big enough for all our bundles then we go into adjusting mode
        return numberOfBundles > CACHE_SIZE;
    }
}
