/*-
 * #%L
 * anchor-mpp-feature
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

package org.anchoranalysis.mpp.feature.bean.energy.scheme;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.shared.dictionary.DictionaryProvider;
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.bean.operator.Sum;
import org.anchoranalysis.image.feature.input.FeatureInputStack;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.feature.addcriteria.AddCriteriaPair;
import org.anchoranalysis.mpp.feature.energy.scheme.EnergyScheme;
import org.anchoranalysis.mpp.feature.input.FeatureInputAllMemo;
import org.anchoranalysis.mpp.feature.input.FeatureInputPairMemo;
import org.anchoranalysis.mpp.feature.input.FeatureInputSingleMemo;

public class EnergySchemeCreatorByElement extends EnergySchemeCreator {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private FeatureListProvider<FeatureInputSingleMemo> elemIndCreator;

    @BeanField @Getter @Setter private FeatureListProvider<FeatureInputPairMemo> elemPairCreator;

    @BeanField @OptionalBean @Getter @Setter
    private FeatureListProvider<FeatureInputAllMemo> elemAllCreator;

    @BeanField @Getter @Setter
    private List<NamedBean<FeatureListProvider<FeatureInputStack>>> listImageFeatures =
            new ArrayList<>();

    @BeanField @Getter @Setter private AddCriteriaPair pairAddCriteria;

    @BeanField @Getter @Setter private RegionMap regionMap;

    @BeanField @OptionalBean @Getter @Setter private DictionaryProvider dictionary;

    /**
     * If true, the names of the imageFeatures are taken as a combination of the namedItem and the
     * actual features
     */
    @BeanField @Getter @Setter private boolean includeFeatureNames = false;
    // END BEAN PROPERTIES

    @Override
    public EnergyScheme create() throws CreateException {
        try {
            return new EnergyScheme(
                    elemIndCreator.get(),
                    elemPairCreator.get(),
                    createAll(),
                    regionMap,
                    pairAddCriteria,
                    Optional.ofNullable(dictionary),
                    buildImageFeatures());
        } catch (ProvisionFailedException e) {
            throw new CreateException(e);
        }
    }

    private FeatureList<FeatureInputAllMemo> createAll() throws CreateException {
        try {
            if (elemAllCreator != null) {
                return elemAllCreator.get();
            } else {
                return FeatureListFactory.empty();
            }
        } catch (ProvisionFailedException e) {
            throw new CreateException(e);
        }
    }

    private List<NamedBean<Feature<FeatureInputStack>>> buildImageFeatures()
            throws CreateException {
        try {
            return FunctionalList.mapToList(
                    listImageFeatures,
                    ProvisionFailedException.class,
                    ni -> sumList(ni.getValue().get(), ni.getName()));
        } catch (ProvisionFailedException e) {
            throw new CreateException(e);
        }
    }

    private NamedBean<Feature<FeatureInputStack>> sumList(
            FeatureList<FeatureInputStack> fl, String name) {
        Sum<FeatureInputStack> feature = new Sum<>(fl);
        return new NamedBean<>(nameForFeature(feature, name), feature);
    }

    private String nameForFeature(Feature<?> feature, String name) {

        if (includeFeatureNames) {
            return String.format("%s.%s", name, feature.getFriendlyName());
        } else {
            return name;
        }
    }
}
