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
/* (C)2020 */
package org.anchoranalysis.feature.session.cache.horizontal;

import java.util.HashMap;
import java.util.Map;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.cache.calculation.ResettableCalculation;

class ResettableSet<T extends ResettableCalculation> {

    /** A map for finding identical objects */
    private Map<T, T> map = new HashMap<>();

    /** Do we log cache events or not? */
    private boolean doLogging = false;

    public ResettableSet(boolean doLogging) {
        super();
        this.doLogging = doLogging;
    }

    /**
     * Finds an existing object if its parameters match, otherwise adds target to the list.
     *
     * @param target
     * @param logger if non-NULL logging messages are written out indicating if the object was added
     *     or not
     * @return an existing CachedCalculation if found, otherwise target if added
     */
    public T findOrAdd(T target, Logger logger) {

        T existing = map.get(target);

        if (existing == null) {

            if (doLogging && logger != null) {
                logger.messageLogger()
                        .logFormatted("Cache-addding: %s (%d)", target, target.hashCode());
            }

            map.put(target, target);

            return target;

        } else {

            // Reusing an existing item
            if (doLogging && logger != null) {
                logger.messageLogger()
                        .logFormatted("Cache-reusing: %s (%d)", existing, existing.hashCode());
            }

            return existing;
        }
    }

    public void invalidate() {
        for (T cachedCalculation : map.values()) {
            cachedCalculation.invalidate();
        }
    }

    public int size() {
        return map.size();
    }

    public String describe() {
        StringBuilder sb = new StringBuilder();
        for (T item : map.values()) {
            sb.append(String.format("%s: %s%n", System.identityHashCode(item), item.toString()));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return describe();
    }
}
