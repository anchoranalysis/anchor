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
/* (C)2020 */
package org.anchoranalysis.feature.bean.operator;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.descriptor.FeatureInputType;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class FeatureListElem<T extends FeatureInput> extends Feature<T> {

    // START BEAN PARAMETERS
    @BeanField @Getter private List<Feature<T>> list = new ArrayList<>();
    // END BEAN PARAMETERS

    /**
     * Constructor
     *
     * @param featureList feature-list
     */
    protected FeatureListElem(FeatureList<T> featureList) {
        this.list = featureList.asList();
    }

    /**
     * A string description of all the items of the list concatenated together with a character in
     * between
     *
     * @param list
     * @param operatorDscr
     * @return
     */
    protected String descriptionForList(String operatorDscr) {
        return String.join(operatorDscr, FunctionalList.mapToList(list, Feature::getDscrLong));
    }

    public void setList(List<Feature<T>> list) {
        this.list = list;
    }

    public void setList(FeatureList<T> list) {
        this.list = list.asList();
    }

    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputType.determineInputType(list);
    }
}
