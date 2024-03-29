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
package org.anchoranalysis.feature.io.results.group;

import java.util.Optional;
import org.anchoranalysis.feature.input.FeatureInputResults;
import org.anchoranalysis.feature.io.csv.metadata.FeatureCSVMetadataForOutput;
import org.anchoranalysis.feature.io.csv.results.FeatureCSVWriterFactory;
import org.anchoranalysis.feature.io.csv.results.LabelledResultsCSVWriter;
import org.anchoranalysis.feature.io.csv.results.LabelledResultsCSVWriterFactory;
import org.anchoranalysis.feature.io.results.FeatureOutputMetadata;
import org.anchoranalysis.feature.io.results.LabelledResultsCollector;
import org.anchoranalysis.feature.io.results.LabelledResultsVector;
import org.anchoranalysis.feature.store.NamedFeatureStore;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.InputOutputContextSubdirectoryCache;

/**
 * Stores feature-calculation results, indexing by their group.
 *
 * <p>Outputs various files pertaining to the grouped-features to the filesystem.
 *
 * <p>Two categories of outputs occur:
 *
 * <ul>
 *   <li>Aggregate output (a CSV file showing aggregate features for <i>all</i> groups).
 *   <li>Group outputs (XML and specific CSV files with features only for the group).
 * </ul>
 *
 * @author Owen Feehan
 */
public class GroupedResults {

    /**
     * A map which stores an aggregate structure for all entries (based on their unique names) and
     * also on an aggregation-key extracted from the name
     */
    private ResultsMap map = new ResultsMap();

    /**
     * Adds a result to the group-writer, but doesn't write yet.
     *
     * @param results the results.
     */
    public void addResultsFor(LabelledResultsVector results) {
        map.addResultsFor(results);
    }

    /**
     * Writes outputs for groups that have been previously added with {@link #addResultsFor}.
     *
     * @param featuresAggregate features for aggregating existing results-calculations, if enabled.
     * @param includeGroups whether to output "groups".
     * @param outputMetadata additional information needed for the outputs in {@link
     *     LabelledResultsCollector}.
     * @param createAggregatedCSVWriter creating a CSVWriter for the aggregated outputs.
     * @param contextGroups input-output context for the group outputs.
     * @throws OutputWriteFailedException if any Writing fails.
     */
    public void writeGroupResults(
            Optional<NamedFeatureStore<FeatureInputResults>> featuresAggregate,
            boolean includeGroups,
            FeatureOutputMetadata outputMetadata,
            FeatureCSVWriterFactory createAggregatedCSVWriter,
            InputOutputContextSubdirectoryCache contextGroups)
            throws OutputWriteFailedException {
        if (includeGroups) {
            writeGroupXMLAndIntoCSV(outputMetadata, contextGroups);
        }

        if (featuresAggregate.isPresent()) {
            writeAggregated(
                    featuresAggregate.get(),
                    outputMetadata,
                    createAggregatedCSVWriter,
                    contextGroups);
        }
    }

    private void writeGroupXMLAndIntoCSV(
            FeatureOutputMetadata outputMetadata,
            InputOutputContextSubdirectoryCache contextGroups) {
        outputMetadata
                .outputNames()
                .getCsvFeaturesGroup()
                .ifPresent(
                        outputName -> {
                            WriteCSVForGroup groupedCSVWriter =
                                    new WriteCSVForGroup(
                                            outputName,
                                            outputMetadata.featureNamesNonAggregated(),
                                            contextGroups);
                            map.iterateResults(groupedCSVWriter::write);
                        });
    }

    private void writeAggregated(
            NamedFeatureStore<FeatureInputResults> featuresAggregate,
            FeatureOutputMetadata outputMetadata,
            FeatureCSVWriterFactory aggregatedCSVWriterCreator,
            InputOutputContextSubdirectoryCache contextGroups)
            throws OutputWriteFailedException {

        if (map.isEmpty()) {
            // NOTHING TO DO, exit early
            return;
        }

        Optional<FeatureCSVMetadataForOutput> metadataAggregated =
                outputMetadata.csvAggregated(featuresAggregate.featureNames());

        if (!metadataAggregated.isPresent()) {
            // NOTHING TO DO, exit early
            return;
        }

        LabelledResultsCSVWriter aggregatedResults =
                LabelledResultsCSVWriterFactory.create(
                        metadataAggregated.get(),
                        aggregatedCSVWriterCreator,
                        Optional.empty(),
                        true);

        aggregatedResults.start();
        try {
            map.iterateResults(
                    (groupName, results) -> {
                        if (!results.isEmpty()) {
                            new WriteAggregatedForGroup(featuresAggregate, results)
                                    .maybeWrite(
                                            groupName,
                                            outputMetadata,
                                            Optional.of(aggregatedResults),
                                            contextGroups);
                        }
                    });
        } finally {
            aggregatedResults.end();
        }
    }
}
