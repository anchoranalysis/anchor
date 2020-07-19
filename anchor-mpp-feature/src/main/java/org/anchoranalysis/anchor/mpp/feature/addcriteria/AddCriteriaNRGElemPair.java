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

package org.anchoranalysis.anchor.mpp.feature.addcriteria;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.feature.nrg.NRGPair;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalculationException;
import org.anchoranalysis.feature.calc.NamedFeatureCalculationException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.nrg.NRGTotal;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;

public class AddCriteriaNRGElemPair implements AddCriteria<NRGPair> {

    private FeatureList<FeatureInputPairMemo> nrgElemPairList;

    // List of criteria for adding pairs
    private AddCriteriaPair pairAddCriteria;

    private Optional<FeatureList<FeatureInputPairMemo>> featuresAddCriteria;

    public AddCriteriaNRGElemPair(
            FeatureList<FeatureInputPairMemo> nrgElemPairList, AddCriteriaPair pairAddCriteria)
            throws InitException {
        super();

        this.nrgElemPairList = nrgElemPairList;
        this.pairAddCriteria = pairAddCriteria;

        try {
            this.featuresAddCriteria = this.pairAddCriteria.orderedListOfFeatures();
        } catch (CreateException e) {
            throw new InitException(e);
        }
    }

    /** @throws CreateException */
    @Override
    public Optional<FeatureList<FeatureInputPairMemo>> orderedListOfFeatures()
            throws CreateException {
        return Optional.of(nrgElemPairList.shallowDuplicate().append(featuresAddCriteria));
    }

    // Returns NULL if to reject an edge
    @Override
    public Optional<NRGPair> generateEdge(
            VoxelizedMarkMemo mark1,
            VoxelizedMarkMemo mark2,
            NRGStackWithParams nrgStack,
            Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session,
            boolean do3D)
            throws CreateException {

        // We have to split our FeatureSession in two seperate sessions:
        //       some features for the includeMarks
        //   and some features for nrgElemPairList

        // If any of the add criteria indicate an edge, then we calc the features
        //  This will also ensure the params collection is fully populated with
        //  necessary calculations from the addCriteria calculations to be used later
        boolean calc = false;
        try {
            if (pairAddCriteria.includeMarks(
                    mark1, mark2, nrgStack.getDimensions(), session, do3D)) {
                calc = true;
            }
        } catch (IncludeMarksFailureException e) {
            throw new CreateException(e);
        }

        if (calc) {
            try {
                FeatureInputPairMemo params = new FeatureInputPairMemo(mark1, mark2, nrgStack);
                ResultsVector rv =
                        session.orElseThrow(
                                        () ->
                                                new NamedFeatureCalculationException(
                                                        "No feature-evaluator exists"))
                                .calc(params, nrgElemPairList);

                Pair<Mark> pair = new Pair<>(mark1.getMark(), mark2.getMark());
                return Optional.of(new NRGPair(pair, new NRGTotal(rv.total())));
            } catch (NamedFeatureCalculationException e) {
                throw new CreateException(e);
            }
        } else {
            return Optional.empty();
        }
    }
}
