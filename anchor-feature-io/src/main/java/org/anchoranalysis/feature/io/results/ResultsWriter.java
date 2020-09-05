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

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;
import org.anchoranalysis.feature.calculate.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInputResults;
import org.anchoranalysis.feature.io.csv.FeatureCSVWriter;
import org.anchoranalysis.feature.io.csv.RowLabels;
import org.anchoranalysis.feature.io.results.group.GroupWriter;
import org.anchoranalysis.feature.list.NamedFeatureStore;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.sequencetype.SetSequenceType;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.CacheSubdirectoryContext;

/**
 * Feature calculation results that can be outputted in different ways.
 * 
 * The outputs are (based upon the default output-names in {@link ResultsWriterOutputNames}):
 * <pre>
 * Two CSV files outputted:
 *    features.csv:              all the features in a single CSV file
 *    featuresAggregated.csv:    aggregate-functions applied to each group of features (based upon their group identifier)
 *
 * Additionally, the following files might be outputted for each group
 *    featuresGroup.csv:            the features for a particular-group
 *    featuresAggregatedGroup.xml   the aggregate-functions applied to this particular-group (in an XML format)
 * </pre>
 * 
 * @author Owen Feehan
 *
 */
public class ResultsWriter implements Closeable {

    /** The highest-level group directory */
    private static final ManifestFolderDescription MANIFEST_GROUP_ROOT =
            new ManifestFolderDescription(
                    "groupedResultsRoot", "featureCsv", new SetSequenceType());

    /** The second highest-level group directory */
    private static final ManifestFolderDescription MANIFEST_GROUP_SUBROOT =
            new ManifestFolderDescription("groupedResults", "featureCsv", new SetSequenceType());

    private Optional<FeatureCSVWriter> writer;
    private GroupWriter groupWriter;

    /**
     * Creates for metadata and in a particular directory.
     * 
     * @param outputMetadata metadata needed for determing output-names and CSV headers.
     * @param context defines the direction in which outputs occur.
     * @throws AnchorIOException if I/O fails.
     */
    public ResultsWriter(
            ResultsWriterMetadata outputMetadata, BoundIOContext context)
            throws AnchorIOException {

        // Where non-group results are outputted
        writer = FeatureCSVWriter.create(outputMetadata.metadataNonAggregated(), context.getOutputManager());
        
        // Where group results are outputted
        groupWriter = new GroupWriter(outputMetadata);
    }

    /**
     * Adds feature-calculation results to be written.
     * 
     * <p>Depending on the output type, these results are either immediately written or
     * else stored in memory to be written later (collectively after all results have been added).
     * 
     * @param labels labels that identify the results.
     * @param results the results.
     */
    public void addResultsFor(RowLabels labels, ResultsVector results) {

        // Place into the group writer, to be written later
        groupWriter.addResultsFor(labels, results);

        // Write feature-value directly into CSV
        if (writer.isPresent()) {
            writer.get().addRow(labels, results);
        }
    }

    /**
     * Writes the stored-results to CSV files.
     *
     * @param featuresAggregate aggregate-features
     * @param includeGroups iff true a group-column is included in the CSV file and the group
     *     exports occur, otherwise not
     * @param context input-output context
     * @throws AnchorIOException if any input-output errors occur
     */
    public void writeResultsForAllGroups(
            Optional<NamedFeatureStore<FeatureInputResults>> featuresAggregate,
            boolean includeGroups,
            BoundIOContext context)
            throws AnchorIOException {

        CacheSubdirectoryContext contextGroups =
                new CacheSubdirectoryContext(
                        context.subdirectory("grouped", MANIFEST_GROUP_ROOT),
                        MANIFEST_GROUP_SUBROOT);

        groupWriter.writeGroupResults(featuresAggregate, includeGroups, context, contextGroups);
    }
    
    @Override
    public void close() throws IOException {
        writer.ifPresent(FeatureCSVWriter::close);
    }
}
