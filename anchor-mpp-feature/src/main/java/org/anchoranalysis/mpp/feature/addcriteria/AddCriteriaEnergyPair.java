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

package org.anchoranalysis.mpp.feature.addcriteria;

import java.util.Optional;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorMulti;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.results.ResultsVector;
import org.anchoranalysis.mpp.feature.energy.EnergyPair;
import org.anchoranalysis.mpp.feature.energy.EnergyTotal;
import org.anchoranalysis.mpp.feature.input.FeatureInputPairMemo;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.mpp.pair.MarkPair;

public class AddCriteriaEnergyPair implements AddCriteria<EnergyPair> {

    private FeatureList<FeatureInputPairMemo> energyPairs;

    // List of criteria for adding pairs
    private AddCriteriaPair pairAddCriteria;

    private Optional<FeatureList<FeatureInputPairMemo>> featuresAddCriteria;

    public AddCriteriaEnergyPair(
            FeatureList<FeatureInputPairMemo> energyPairs, AddCriteriaPair pairAddCriteria)
            throws InitializeException {
        super();

        this.energyPairs = energyPairs;
        this.pairAddCriteria = pairAddCriteria;

        try {
            this.featuresAddCriteria = this.pairAddCriteria.orderedListOfFeatures();
        } catch (CreateException e) {
            throw new InitializeException(e);
        }
    }

    /** @throws CreateException */
    @Override
    public Optional<FeatureList<FeatureInputPairMemo>> orderedListOfFeatures()
            throws CreateException {
        return Optional.of(energyPairs.shallowDuplicate().append(featuresAddCriteria));
    }

    // Returns null if to reject an edge
    @Override
    public Optional<EnergyPair> generateEdge(
            VoxelizedMarkMemo mark1,
            VoxelizedMarkMemo mark2,
            EnergyStack energyStack,
            Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session,
            boolean do3D)
            throws CreateException {

        // We have to split our FeatureSession in two separate sessions:
        //       some features for the includeMarks
        //   and some features for energyPairs

        // If any of the add criteria indicate an edge, then we calculate the features
        //  This will also ensure the input collection is fully populated with
        //  necessary calculations from the addCriteria calculations to be used later
        boolean calculate = false;
        try {
            if (pairAddCriteria.includeMarks(
                    mark1, mark2, energyStack.dimensions(), session, do3D)) {
                calculate = true;
            }
        } catch (IncludeMarksFailureException e) {
            throw new CreateException(e);
        }

        if (calculate) {
            try {
                FeatureInputPairMemo input = new FeatureInputPairMemo(mark1, mark2, energyStack);
                ResultsVector results =
                        session.orElseThrow(
                                        () ->
                                                new NamedFeatureCalculateException(
                                                        "No feature-evaluator exists"))
                                .calculate(input, energyPairs);

                MarkPair<Mark> pair = new MarkPair<>(mark1.getMark(), mark2.getMark());
                return Optional.of(new EnergyPair(pair, new EnergyTotal(results.total())));
            } catch (NamedFeatureCalculateException e) {
                throw new CreateException(e);
            }
        } else {
            return Optional.empty();
        }
    }
}
