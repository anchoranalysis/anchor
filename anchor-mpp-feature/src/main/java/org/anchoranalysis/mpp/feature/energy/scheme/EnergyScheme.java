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
import org.anchoranalysis.bean.shared.dictionary.DictionaryProvider;
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.image.feature.input.FeatureInputStack;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.feature.addcriteria.AddCriteriaPair;
import org.anchoranalysis.mpp.feature.input.FeatureInputAllMemo;
import org.anchoranalysis.mpp.feature.input.FeatureInputPairMemo;
import org.anchoranalysis.mpp.feature.input.FeatureInputSingleMemo;

/**
 * The energy for a feature-list as factorized into different cliques.
 *
 * <p>elemInd: individual terms of clique-size==1 f_1(x)
 * elemPair: pairwise terms of clique-size==2 f_2(x,y)
 * elemAll: terms that include every item in the set f_all(x_1,x_2,....x_n) for all n
 *
 * @author Owen Feehan
 */
public class EnergyScheme {

    /** Features for individual elements (clique-size==1). */
    private final FeatureList<FeatureInputSingleMemo> elemInd;

    /** Features for pairs of elements (clique-size==2). */
    private final FeatureList<FeatureInputPairMemo> elemPair;

    /** Features for all elements together. */
    private final FeatureList<FeatureInputAllMemo> elemAll;

    /** The region map used in the energy scheme. */
    private final RegionMap regionMap;

    /**
     * A list of features of the image that are calculated first, and exposed to the other features
     * as parameters.
     */
    private final List<NamedBean<Feature<FeatureInputStack>>> listImageFeatures;

    /** Criteria for adding pairs to the energy calculation. */
    private final AddCriteriaPair pairAddCriteria;

    /** Optional dictionary provider for the energy scheme. */
    private final Optional<DictionaryProvider> dictionary;

    /**
     * Creates an energy scheme with the specified features and region map.
     *
     * @param elemInd features for individual elements
     * @param elemPair features for pairs of elements
     * @param elemAll features for all elements together
     * @param regionMap the region map to use
     * @param pairAddCriteria criteria for adding pairs
     * @throws CreateException if the energy scheme cannot be created
     */
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

    /**
     * Creates an energy scheme with the specified features, region map, and additional options.
     *
     * @param elemInd features for individual elements
     * @param elemPair features for pairs of elements
     * @param elemAll features for all elements together
     * @param regionMap the region map to use
     * @param pairAddCriteria criteria for adding pairs
     * @param dictionary optional dictionary provider
     * @param listImageFeatures list of image features to be calculated first
     * @throws CreateException if the energy scheme cannot be created
     */
    public EnergyScheme(
            FeatureList<FeatureInputSingleMemo> elemInd,
            FeatureList<FeatureInputPairMemo> elemPair,
            FeatureList<FeatureInputAllMemo> elemAll,
            RegionMap regionMap,
            AddCriteriaPair pairAddCriteria,
            Optional<DictionaryProvider> dictionary,
            List<NamedBean<Feature<FeatureInputStack>>> listImageFeatures)
            throws CreateException {
        this.elemInd = elemInd;
        this.elemPair = elemPair;
        this.elemAll = elemAll;
        this.regionMap = regionMap;
        this.pairAddCriteria = pairAddCriteria;
        this.dictionary = dictionary;
        this.listImageFeatures = listImageFeatures;
        checkAtLeastOneEnergyElement();
    }

    /**
     * Creates and returns the associated {@link Dictionary} or an empty dictionary if none is associated.
     *
     * @return the created {@link Dictionary}
     * @throws CreateException if the dictionary cannot be created
     */
    public Dictionary createDictionary() throws CreateException {
        if (dictionary.isPresent()) {
            try {
                return dictionary.get().get().duplicate();
            } catch (ProvisionFailedException e) {
                throw new CreateException(e);
            }
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

    /**
     * Gets the feature list for a specific clique size.
     *
     * @param cliqueSize 1 for pairwise, 0 for unary, -1 for all
     * @return the {@link FeatureList} for the specified clique size
     * @param <T> the type of {@link FeatureInput}
     * @throws AnchorImpossibleSituationException if an invalid clique size is provided
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

    /**
     * Gets the feature list for individual elements.
     *
     * @return the {@link FeatureList} for individual elements
     */
    public FeatureList<FeatureInputSingleMemo> getElemIndAsFeatureList() {
        return elemInd;
    }

    /**
     * Gets the feature list for pairs of elements.
     *
     * @return the {@link FeatureList} for pairs of elements
     */
    public FeatureList<FeatureInputPairMemo> getElemPairAsFeatureList() {
        return elemPair;
    }

    /**
     * Gets the feature list for all elements together.
     *
     * @return the {@link FeatureList} for all elements
     */
    public FeatureList<FeatureInputAllMemo> getElemAllAsFeatureList() {
        return elemAll;
    }

    /**
     * Gets the criteria for adding pairs to the energy calculation.
     *
     * @return the {@link AddCriteriaPair} for pair addition
     */
    public AddCriteriaPair getPairAddCriteria() {
        return pairAddCriteria;
    }

    /**
     * Gets the list of image features to be calculated first.
     *
     * @return the list of {@link NamedBean}s containing image features
     */
    public List<NamedBean<Feature<FeatureInputStack>>> getListImageFeatures() {
        return listImageFeatures;
    }

    /**
     * Gets the region map used in the energy scheme.
     *
     * @return the {@link RegionMap} for the energy scheme
     */
    public RegionMap getRegionMap() {
        return regionMap;
    }
}