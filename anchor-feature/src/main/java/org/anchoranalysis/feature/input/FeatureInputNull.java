/* (C)2020 */
package org.anchoranalysis.feature.input;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

// When we don't care about parameters

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeatureInputNull implements FeatureInput {

    private static final FeatureInputNull INSTANCE = new FeatureInputNull();

    public static FeatureInputNull instance() {
        return INSTANCE;
    }
}
