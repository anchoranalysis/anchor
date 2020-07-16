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
