/*-
 * #%L
 * anchor-feature
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

package org.anchoranalysis.feature.input;

import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.feature.bean.Feature;

/**
 * Performs checks and aggregations on feature-input types are compatible with others
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeatureInputType {

    /**
     * Can these parameters be used with a particular feature?
     *
     * @param sourceType type of input
     * @param otherType type of other
     * @return true iff the feature-input is compatible with {@code paramTypeClass}
     */
    public static boolean isCompatibleWith(
            Class<? extends FeatureInput> sourceType, Class<? extends FeatureInput> otherType) {
        return sourceType.isAssignableFrom(otherType);
    }

    /**
     * Prefer to keep descriptor whose input-class is a sub-class rather than a super-class
     *
     * <p>The order of the two {@code inputType} parameters is irrelevant.
     *
     * @param inputType1 first input-type
     * @param inputType2 second input-type
     * @return the favored input-type of the two, or Optional.empty() if there is no compatibility
     */
    public static Optional<Class<? extends FeatureInput>> prefer(
            Class<? extends FeatureInput> inputType1, Class<? extends FeatureInput> inputType2) {
        // If they are of identical class, exit early
        if (inputType1.equals(inputType2)) {
            return Optional.of(inputType1);
        }

        if (isCompatibleWith(inputType1, inputType2)) {
            return Optional.of(inputType2);
        }

        if (isCompatibleWith(inputType2, inputType1)) { // NOSONAR
            return Optional.of(inputType1);
        }

        return Optional.empty();
    }

    /**
     * Like {@link FeatureInputType#prefer(Class, Class)} but accepts features directly as input.
     *
     * <p>The order of the two {@code inputType} parameters is irrelevant.
     *
     * @param feature1 first-feature
     * @param feature2 second-feature
     * @return the favored input-type of the two, or Optional.empty() if there is no compatibility
     */
    public static Class<? extends FeatureInput> prefer(Feature<?> feature1, Feature<?> feature2) {
        return determineInputType(feature1.inputType(), feature2.inputType());
    }

    /**
     * Finds a common input-type for two classes, throwing an exception if the two types aren't
     * compatible.
     *
     * @param inputType1 first input-type
     * @param inputType2 second input-type
     * @return the favored input-type of the two
     * @throws AnchorFriendlyRuntimeException if they aren't compatible types
     */
    public static Class<? extends FeatureInput> determineInputType(
            Class<? extends FeatureInput> inputType1, Class<? extends FeatureInput> inputType2) {
        Optional<Class<? extends FeatureInput>> preferred = prefer(inputType1, inputType2);
        if (!preferred.isPresent()) {
            throw new AnchorFriendlyRuntimeException(
                    "item1 and item2 must accept the same paramType, or be compatible.");
        }
        return preferred.get();
    }

    /**
     * Finds a common input-type for classes in the list, throwing an exception if any two types
     * aren't compatible.
     *
     * @param <T> common parent input-type on the list (all elements in the list must have a
     *     input-type that sub-classes this)
     * @param list list of features to determine a common input-type for
     * @return the commin input-type
     * @throws AnchorFriendlyRuntimeException if they are any two incompatible types
     */
    public static <T extends FeatureInput> Class<? extends FeatureInput> determineInputType(
            List<Feature<T>> list) {
        if (list.isEmpty()) {
            return FeatureInput.class;
        }

        Class<? extends FeatureInput> chosenParamType = FeatureInput.class;
        for (Feature<?> f : list) {

            Class<? extends FeatureInput> paramType = f.inputType();
            if (!chosenParamType.equals(paramType)) {

                Optional<Class<? extends FeatureInput>> preferred =
                        prefer(paramType, chosenParamType);
                if (!preferred.isPresent()) {
                    // We don't know which parameter to prefer
                    throw new AnchorFriendlyRuntimeException(
                            "All features in the list must have the same paramType, or a simple type, or a preference between conflicting type");
                }
                chosenParamType = preferred.get();
            }
        }
        return chosenParamType;
    }
}
