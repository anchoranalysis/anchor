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

package org.anchoranalysis.feature.bean.operator;

import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.FeatureInputType;

/**
 * A base class for a {@link Feature} that is a function of the results from a list of other
 * features.
 *
 * @author Owen Feehan
 * @param <T> feature input-type of all features in the list, as well as the returned result.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class FeatureFromList<T extends FeatureInput> extends Feature<T> {

    // START BEAN PARAMETERS
    /** The features whose results will be somehow combined, to form the result of this class. */
    @BeanField @Getter private List<Feature<T>> list = Arrays.asList();
    // END BEAN PARAMETERS

    /**
     * Create from a list of features.
     *
     * @param featureList the list of features.
     */
    protected FeatureFromList(FeatureList<T> featureList) {
        this.list = featureList.asList();
    }

    /**
     * Derive a string description of all the items of the list concatenated together.
     *
     * <p>Each feature's individual description is separated from the next by {@code joinCharacter}.
     *
     * @param join the character to separate items in the list.
     * @return the description, as above.
     */
    protected String descriptionForList(String join) {
        return String.join(join, FunctionalList.mapToList(list, Feature::descriptionLong));
    }

    /**
     * Assigns the list of features to use.
     *
     * @param list the list to assign.
     */
    public void setList(List<Feature<T>> list) {
        this.list = list;
    }

    /**
     * Assigns the list of features to use.
     *
     * @param list the list to assign.
     */
    public void setList(FeatureList<T> list) {
        this.list = list.asList();
    }

    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputType.determineInputType(list);
    }
}
