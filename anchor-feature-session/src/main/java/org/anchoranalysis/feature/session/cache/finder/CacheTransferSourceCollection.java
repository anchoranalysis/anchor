/*-
 * #%L
 * anchor-feature-session
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

package org.anchoranalysis.feature.session.cache.finder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.anchoranalysis.feature.calculate.cache.ChildCacheName;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * A collection of existing caches that can <i>collectively</i> be used as sources for child-caches
 * elsewhere.
 *
 * @author Owen Feehan
 */
public class CacheTransferSourceCollection
        implements Iterable<CacheTransferSource<? extends FeatureInput>> {

    /** The names of all child-caches that are made available. */
    private Set<ChildCacheName> allCacheNames = new HashSet<>();

    /** All the sources that are searched for child-caches. */
    private List<CacheTransferSource<? extends FeatureInput>> list = new LinkedList<>();

    /**
     * Adds a source.
     *
     * @param source the source to add.
     */
    public void add(CacheTransferSource<? extends FeatureInput> source) {
        list.add(source);
        allCacheNames.addAll(source.getCacheNames());
    }

    /**
     * Whether a particular child-cache exists in the source?
     *
     * @param name the name of the child-cache.
     * @return true iff the child-cache exists.
     */
    public boolean contains(ChildCacheName name) {
        return allCacheNames.contains(name);
    }

    @Override
    public Iterator<CacheTransferSource<? extends FeatureInput>> iterator() {
        return list.iterator();
    }

    /**
     * The names of all child-caches that are made available.
     *
     * @return a set of names, which should not be modified.
     */
    public Set<ChildCacheName> getAllCacheNames() {
        return allCacheNames;
    }
}
