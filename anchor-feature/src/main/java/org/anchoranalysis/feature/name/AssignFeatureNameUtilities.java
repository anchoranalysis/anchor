package org.anchoranalysis.feature.name;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Utilities for assigning a name to a feature.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssignFeatureNameUtilities {

    /**
     * Assigns a new custom-name to a feature, by combining an existing-name with a prefix.
     *
     * @param feature the feature to assign a custom-name to.
     * @param existingName the existing name.
     * @param prefix the prefix to prepend.
     */
    public static void assignWithPrefix(
            Feature<? extends FeatureInput> feature, String existingName, String prefix) {
        feature.setCustomName(String.format("%s%s", prefix, existingName));
    }
}
