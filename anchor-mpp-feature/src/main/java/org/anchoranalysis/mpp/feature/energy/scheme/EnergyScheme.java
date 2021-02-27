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

package org.anchoranalysis.mpp.feature.energy.scheme;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.image.feature.input.FeatureInputStack;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.feature.addcriteria.AddCriteriaPair;
import org.anchoranalysis.mpp.feature.input.memo.FeatureInputAllMemo;
import org.anchoranalysis.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.mpp.feature.input.memo.FeatureInputSingleMemo;

/**
 * The energy for a feature-list as factorized into different cliques.
 *
 * <p>elemInd: individual terms of clique-size==1 f_1(x) elemPair: pairwise terms of clique-size==2
 * f_2(x,y) elemAll: terms that include every item in the set f_all(x_1,x_2,....x_n) for all n
 *
 * @author Owen Feehan
 */
public class EnergyScheme {

    private final FeatureList<FeatureInputSingleMemo> elemInd;
    private final FeatureList<FeatureInputPairMemo> elemPair;
    private final FeatureList<FeatureInputAllMemo> elemAll;

    private final RegionMap regionMap;

    /**
     * A list of features of the image that are calculated first, and exposed to the other features
     * as parameters
     */
    private final List<NamedBean<Feature<FeatureInputStack>>> listImageFeatures;

    private final AddCriteriaPair pairAddCriteria;

    private final Optional<KeyValueParamsProvider> params;

    public EnergyScheme(
            FeatureList<FeatureInputSingleMemo> elemInd,
            FeatureList<FeatureInputPairMemo> elemPair,
            FeatureList<FeatureInputAllMemo> elemAll,
            RegionMap regionMap,
            AddCriteriaPair pairAddCriteria)
            throws CreateException {
        this(
                elemInd,
                elemPair,
                elemAll,
                regionMap,
                pairAddCriteria,
                Optional.empty(),
                new ArrayList<>());
    }

    public EnergyScheme(
            FeatureList<FeatureInputSingleMemo> elemInd,
            FeatureList<FeatureInputPairMemo> elemPair,
            FeatureList<FeatureInputAllMemo> elemAll,
            RegionMap regionMap,
            AddCriteriaPair pairAddCriteria,
            Optional<KeyValueParamsProvider> keyValueParamsProvider,
            List<NamedBean<Feature<FeatureInputStack>>> listImageFeatures)
            throws CreateException {
        this.elemInd = elemInd;
        this.elemPair = elemPair;
        this.elemAll = elemAll;
        this.regionMap = regionMap;
        this.pairAddCriteria = pairAddCriteria;
        this.params = keyValueParamsProvider;
        this.listImageFeatures = listImageFeatures;
        checkAtLeastOneEnergyElement();
    }

    /*** returns the associated KeyValueParams or an empty set, if no params are associated with the energyScheme */
    public Dictionary createKeyValueParams() throws CreateException {
        if (params.isPresent()) {
            return params.get().create().duplicate();
        } else {
            return new Dictionary();
        }
    }

    // ! Checks that a mark's initial parameters are correct
    private void checkAtLeastOneEnergyElement() throws CreateException {
        if ((elemInd.size() + elemPair.size() + elemAll.size()) == 0) {
            throw new CreateException("At least one Energy element must be specified");
        }
    }

    // -1 means everything

    /**
     * @param cliqueSize 1 for pairwise, 0 for unary, -1 for all
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends FeatureInput> FeatureList<T> getElemByCliqueSize(int cliqueSize) {

        if (cliqueSize == 0) {
            return (FeatureList<T>) getElemIndAsFeatureList();
        } else if (cliqueSize == 1) {
            return (FeatureList<T>) getElemPairAsFeatureList();
        } else if (cliqueSize == -1) {
            return (FeatureList<T>) getElemAllAsFeatureList();
        } else {
            throw new AnchorImpossibleSituationException();
        }
    }

    public FeatureList<FeatureInputSingleMemo> getElemIndAsFeatureList() {
        return elemInd;
    }

    public FeatureList<FeatureInputPairMemo> getElemPairAsFeatureList() {
        return elemPair;
    }

    public FeatureList<FeatureInputAllMemo> getElemAllAsFeatureList() {
        return elemAll;
    }

    public AddCriteriaPair getPairAddCriteria() {
        return pairAddCriteria;
    }

    public List<NamedBean<Feature<FeatureInputStack>>> getListImageFeatures() {
        return listImageFeatures;
    }

    public RegionMap getRegionMap() {
        return regionMap;
    }
}
