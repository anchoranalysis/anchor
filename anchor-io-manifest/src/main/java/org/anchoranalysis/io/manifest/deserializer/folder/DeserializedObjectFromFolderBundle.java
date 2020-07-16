/* (C)2020 */
package org.anchoranalysis.io.manifest.deserializer.folder;

import java.io.Serializable;
import java.util.Map;
import org.anchoranalysis.core.cache.LRUCache;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.ITypedGetFromIndex;
import org.anchoranalysis.core.index.container.OrderProvider;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.deserializer.bundle.Bundle;
import org.anchoranalysis.io.manifest.deserializer.bundle.BundleParameters;
import org.anchoranalysis.io.manifest.deserializer.bundle.BundleUtilities;
import org.anchoranalysis.io.manifest.folder.FolderWrite;

public class DeserializedObjectFromFolderBundle<T extends Serializable>
        implements ITypedGetFromIndex<T> {

    private LRUCache<Integer, Map<Integer, T>> cache;
    private BundleParameters bundleParameters;
    private OrderProvider orderProvider;

    public DeserializedObjectFromFolderBundle(
            FolderWrite folderWrite, final BundleDeserializers<T> deserializers, int cacheSize)
            throws DeserializationFailedException {
        super();

        final FolderWrite bundleFolder = folderWrite;

        // We create our cache
        this.cache =
                new LRUCache<>(
                        cacheSize,
                        index -> {
                            try {
                                Bundle<T> bundle =
                                        BundleUtilities.generateBundle(
                                                deserializers.getDeserializerBundle(),
                                                bundleFolder,
                                                index);
                                return bundle.createHashMap();
                            } catch (IllegalArgumentException | DeserializationFailedException e) {
                                throw new OperationFailedException(e);
                            }
                        });

        bundleParameters =
                BundleUtilities.generateBundleParameters(
                        deserializers.getDeserializerBundleParameters(), folderWrite);

        if (bundleParameters == null) {
            throw new DeserializationFailedException("Cannot find bundle parameters");
        }

        orderProvider = bundleParameters.getSequenceType().createOrderProvider();
    }

    @Override
    public T get(int index) throws GetOperationFailedException {

        // We divide the index by the bundle size, to get whichever bundle we need to retrieve
        int bundleIndex =
                orderProvider.order(String.valueOf(index)) / bundleParameters.getBundleSize();

        // This HashMap can contain NULL keys representing deliberately null objects
        Map<Integer, T> hashMap = this.cache.get(bundleIndex);

        if (!hashMap.containsKey(index)) {
            throw new GetOperationFailedException(
                    String.format("Cannot find index %i in bundle", index));
        }

        return hashMap.get(index);
    }

    public BundleParameters getBundleParameters() {
        return bundleParameters;
    }
}
