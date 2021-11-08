/*-
 * #%L
 * anchor-feature-io
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
package org.anchoranalysis.feature.io.results;

import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.anchoranalysis.feature.input.FeatureInputResults;
import org.anchoranalysis.feature.io.csv.FeatureCSVMetadata;
import org.anchoranalysis.feature.io.results.calculation.FeatureCalculationResults;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.store.NamedFeatureStore;

/**
 * Additional information needed for the outputs in {@link FeatureCalculationResults}.
 *
 * <p>i.e. headers, the output-name etc.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class FeatureOutputMetadata {

    /** Headers for the CSV File */
    @Getter private final LabelHeaders labelHeaders;

    /** Names of each feature in the feature columns. */
    @Getter private final FeatureNameList featureNamesNonAggregate;

    /** Names for any outputs produced by {@link FeatureCalculationResults}. */
    @Getter private final FeatureOutputNames outputNames;

    /**
     * Derives metadata for the non-aggregated CSV output.
     *
     * @return newly created metadata
     */
    public FeatureCSVMetadata metadataNonAggregated() {
        return createMetadata(
                outputNames.getCsvFeaturesNonAggregated(),
                labelHeaders.allHeaders(),
                featureNamesNonAggregate.shallowCopy());
    }

    /**
     * Derives metadata for the aggregated CSV output, if enabled.
     *
     * @param featuresAggregate features to be used for aggregating existing features.
     * @return newly created metadata, or {@code Optional.empty()} if its disabled.
     */
    public Optional<FeatureCSVMetadata> metadataAggregated(
            NamedFeatureStore<FeatureInputResults> featuresAggregate) {
        return outputNames
                .getCsvFeaturesAggregated()
                .map(
                        outputName ->
                                createMetadata(
                                        outputName,
                                        labelHeaders.getGroupHeaders(),
                                        featuresAggregate.createFeatureNames()));
    }

    private static FeatureCSVMetadata createMetadata(
            String outputName,
            String[] nonFeatureHeaders,
            FeatureNameList featureNamesNewlyCreated) {
        featureNamesNewlyCreated.insertBeginning(nonFeatureHeaders);
        return new FeatureCSVMetadata(outputName, featureNamesNewlyCreated.asList());
    }
}
