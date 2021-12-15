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
import org.anchoranalysis.feature.io.csv.metadata.FeatureCSVMetadataForOutput;
import org.anchoranalysis.feature.io.csv.metadata.LabelHeaders;
import org.anchoranalysis.feature.name.FeatureNameList;

/**
 * Information needed for the outputting differnt types of feature-table CSV files.
 *
 * <p>e.g. headers, the output-name etc.
 *
 * <p>Two types of CSV files may be written:
 *
 * <ul>
 *   <li>non-aggregated (using the features directly on the underlying entities)
 *   <li>aggregated (aggregating the results from the above)
 * </ul>
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class FeatureOutputMetadata {

    /** Headers for the CSV File. */
    @Getter private final LabelHeaders labelHeaders;

    /** Names of each feature in the feature columns, for non-aggregated features. */
    @Getter private final FeatureNameList featureNamesNonAggregated;

    /** Names for any outputs produced by {@link LabelledResultsCollector}. */
    @Getter private final FeatureOutputNames outputNames;

    /**
     * Specific metadata for writing a <b>non-aggregated CSV file</b>.
     *
     * @return a newly created {@link FeatureCSVMetadataForOutput}.
     */
    public FeatureCSVMetadataForOutput csvNonAggregated() {
        return new FeatureCSVMetadataForOutput(
                labelHeaders.allHeaders(),
                featureNamesNonAggregated,
                outputNames.getCsvFeaturesNonAggregated());
    }

    /**
     * Specific metadata for writing an <b>aggregated CSV file</b>.
     *
     * @param featureNamesAggregated the feature-names to use for the aggregated CSV file (which
     *     usally differ from {@code featureNamesNonAggregate}.
     * @return a newly created {@link FeatureCSVMetadataForOutput}.
     */
    public Optional<FeatureCSVMetadataForOutput> csvAggregated(
            FeatureNameList featureNamesAggregated) {
        return outputNames
                .getCsvFeaturesAggregated()
                .map(
                        outputName ->
                                new FeatureCSVMetadataForOutput(
                                        labelHeaders.getGroupHeaders(),
                                        featureNamesAggregated,
                                        outputName));
    }
}
