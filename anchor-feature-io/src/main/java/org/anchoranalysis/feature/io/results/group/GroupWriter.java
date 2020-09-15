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
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.feature.calculate.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInputResults;
import org.anchoranalysis.feature.io.csv.FeatureCSVMetadata;
import org.anchoranalysis.feature.io.csv.FeatureCSVWriter;
import org.anchoranalysis.feature.io.csv.RowLabels;
import org.anchoranalysis.feature.io.results.ResultsWriterMetadata;
import org.anchoranalysis.feature.list.NamedFeatureStore;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.CacheSubdirectoryContext;

/**
 * Writes outputs pertaining to groups to the filesystem.
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
@RequiredArgsConstructor
public class GroupWriter {

    // START REQUIRED ARGUMENTS
    private final ResultsWriterMetadata outputMetadata;
    // END REQUIRED ARGUMENTS

    /**
     * A map which stores an aggregate structure for all entries (based on their unique names) and
     * also on an aggregation-key extracted from the name
     */
    private ResultsMap map = new ResultsMap();

    /**
     * Adds a result to the group-writer, but doesn't write yet.
     *
     * @param labels labels for the result
     * @param results the results
     */
    public void addResultsFor(RowLabels labels, ResultsVector results) {
        map.addResultsFor(labels, results);
    }

    /**
     * Writes outputs for groups that have been previously added with {@link #addResultsFor}.
     *
     * @param featuresAggregate features for aggregating existing results-calculations, if enabled
     * @param includeGroups whether to output "groups"
     * @param contextAggregated input-output context for the aggregage outputs
     * @param contextGroups input-output context for the group outputs
     * @throws AnchorIOException if any input-output errors occur
     */
    public void writeGroupResults(
            Optional<NamedFeatureStore<FeatureInputResults>> featuresAggregate,
            boolean includeGroups,
            BoundIOContext contextAggregated,
            CacheSubdirectoryContext contextGroups)
            throws AnchorIOException {
        if (includeGroups) {
            writeGroupXMLAndIntoCSV(contextGroups);
        }

        if (featuresAggregate.isPresent()) {
            writeAggregated(featuresAggregate.get(), contextAggregated, contextGroups);
        }
    }

    private void writeGroupXMLAndIntoCSV(CacheSubdirectoryContext contextGroups) {
        outputMetadata
                .outputNames()
                .getCsvFeaturesGroup()
                .ifPresent(
                        outputName -> {
                            WriteCSVForGroup groupedCSVWriter =
                                    new WriteCSVForGroup(
                                            outputName,
                                            outputMetadata.featureNamesNonAggregate(),
                                            contextGroups);
                            map.iterateResults(groupedCSVWriter::write);
                        });
    }

    private void writeAggregated(
            NamedFeatureStore<FeatureInputResults> featuresAggregate,
            BoundIOContext contextAggregated,
            CacheSubdirectoryContext contextGroups)
            throws AnchorIOException {

        if (map.isEmpty()) {
            // NOTHING TO DO, exit early
            return;
        }

        Optional<FeatureCSVMetadata> csvMetadata =
                outputMetadata.metadataAggregated(featuresAggregate);

        if (!csvMetadata.isPresent()) {
            // NOTHING TO DO, exit early
            return;
        }

        Optional<FeatureCSVWriter> csvWriter =
                FeatureCSVWriter.create(csvMetadata.get(), contextAggregated.getOutputManager());

        try {
            map.iterateResults(
                    (groupName, results) -> {
                        if (results.size() != 0) {
                            new WriteXMLForGroup(featuresAggregate, results)
                                    .maybeWrite(
                                            groupName, outputMetadata, csvWriter, contextGroups);
                        }
                    });
        } finally {
            csvWriter.ifPresent(FeatureCSVWriter::close);
        }
    }
}
