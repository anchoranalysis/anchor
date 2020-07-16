/* (C)2020 */
package org.anchoranalysis.feature.name;

import java.util.HashMap;
import org.anchoranalysis.core.index.GetOperationFailedException;

/**
 * A map of Feature-Names (Strings) to indexes (Int)
 *
 * <p>Implemented as a {@link HashMap}.
 *
 * @author Owen Feehan
 */
public class FeatureNameMapToIndex {

    private HashMap<String, Integer> delegate = new HashMap<>();

    /**
     * Adds a new featureName and index
     *
     * @param featureName
     * @param index
     */
    public void add(String featureName, int index) {
        delegate.put(featureName, index);
    }

    /**
     * The index for a particular featureName
     *
     * @param featureName
     * @return the index
     * @throws GetOperationFailedException
     */
    public int indexOf(String featureName) throws GetOperationFailedException {
        Integer index = delegate.get(featureName);
        if (index == null) {
            throw new GetOperationFailedException(
                    String.format("Cannot find '%s' in FeatureNameIndex", featureName));
        }
        return index;
    }
}
