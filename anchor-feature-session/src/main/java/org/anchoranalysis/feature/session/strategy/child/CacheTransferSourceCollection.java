/* (C)2020 */
package org.anchoranalysis.feature.session.strategy.child;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.input.FeatureInput;

public class CacheTransferSourceCollection
        implements Iterable<CacheTransferSource<? extends FeatureInput>> {

    private Set<ChildCacheName> allCacheNames = new HashSet<>();
    private List<CacheTransferSource<? extends FeatureInput>> list = new ArrayList<>();

    public void add(CacheTransferSource<? extends FeatureInput> source) {
        list.add(source);
        allCacheNames.addAll(source.getCacheNames());
    }

    public boolean contains(ChildCacheName childName) {
        return allCacheNames.contains(childName);
    }

    @Override
    public Iterator<CacheTransferSource<? extends FeatureInput>> iterator() {
        return list.iterator();
    }

    public Set<ChildCacheName> getAllCacheNames() {
        return allCacheNames;
    }
}
