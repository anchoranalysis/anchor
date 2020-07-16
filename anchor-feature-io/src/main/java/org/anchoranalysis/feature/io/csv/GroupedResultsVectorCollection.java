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

package org.anchoranalysis.feature.io.csv;

import com.google.common.collect.Comparators;
import java.io.Closeable;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Optional;
import org.anchoranalysis.core.collection.MapCreate;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.calc.results.ResultsVectorCollection;
import org.anchoranalysis.feature.io.csv.name.MultiName;
import org.anchoranalysis.feature.io.csv.writer.FeatureCSVWriter;
import org.anchoranalysis.feature.list.NamedFeatureStore;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.resultsvectorcollection.FeatureInputResults;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.sequencetype.SetSequenceType;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.CacheSubdirectoryContext;

public class GroupedResultsVectorCollection implements Closeable {

    private static final String OUTPUT_NAME_FEATURES = "features";
    private static final String OUTPUT_NAME_FEATURES_AGGREGATED = "featuresAggregated";

    /** The highest-level group directory */
    private static final ManifestFolderDescription MANIFEST_GROUP_ROOT =
            new ManifestFolderDescription(
                    "groupedResultsRoot", "featureCsv", new SetSequenceType());

    /** The second highest-level group directory */
    private static final ManifestFolderDescription MANIFEST_GROUP_SUBROOT =
            new ManifestFolderDescription("groupedResults", "featureCsv", new SetSequenceType());

    private final MetadataHeaders metadata;
    private final FeatureNameList featureNamesNonAggregate;

    private Optional<FeatureCSVWriter> writer;

    /**
     * A map which stores an aggregate structure for all entries (based on their unique names) and
     * also on an aggregation-key extracted from the name
     */
    private MapCreate<Optional<MultiName>, ResultsVectorCollection> map =
            new MapCreate<>(
                    ResultsVectorCollection::new,
                    Comparators.emptiesFirst(Comparator.naturalOrder()));

    public GroupedResultsVectorCollection(
            MetadataHeaders metadata,
            FeatureNameList featureNamesNonAggregate,
            BoundIOContext context)
            throws AnchorIOException {
        this.metadata = metadata;
        this.featureNamesNonAggregate = featureNamesNonAggregate;

        // Open file for writing features to
        writer =
                FeatureCSVWriter.create(
                        OUTPUT_NAME_FEATURES,
                        context.getOutputManager(),
                        metadata.allHeaders(),
                        featureNamesNonAggregate);
    }

    public void addResultsFor(StringLabelsForCsvRow identifier, ResultsVector rv) {

        // Place into the aggregate structure
        map.getOrCreateNew(identifier.getGroup()).add(rv);

        // Write feature-value directly into CSV
        if (writer.isPresent()) {
            writer.get().addResultsVector(identifier, rv);
        }
    }

    /**
     * Writes the stored-results to CSV files.
     *
     * <pre>
     * Two CSV files are (depending on output settings) outputted:
     *    features.csv:    			 all the features in a single CSV file
     *    featuresAggregated.csv:    aggregate-functions applied to each group of features (based upon their group identifier)
     *
     * Additionally, the following files might be outputted for each group
     *    featuresGroup.csv:  			the features for a particular-group
     *    featuresAggregatedGroup.xml	the aggregate-functions applied to this particular-group (in an XML format)
     * </pre>
     *
     * @param featuresAggregate aggregate-features
     * @param includeGroups iff TRUE a group-column is included in the CSV file and the group
     *     exports occur, otherwise not
     * @throws AnchorIOException
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

        if (includeGroups) {
            writeAllGroups(contextGroups);
        }

        if (featuresAggregate.isPresent()) {
            writeAggregated(featuresAggregate.get(), context, contextGroups);
        }
    }

    private void writeAllGroups(CacheSubdirectoryContext context) {
        for (Entry<Optional<MultiName>, ResultsVectorCollection> entry : map.entrySet()) {
            WriteGroupResults.writeResultsForSingleGroup(
                    entry.getKey(), entry.getValue(), featureNamesNonAggregate, context);
        }
    }

    private void writeAggregated(
            NamedFeatureStore<FeatureInputResults> featuresAggregate,
            BoundIOContext context,
            CacheSubdirectoryContext contextGroups)
            throws AnchorIOException {

        ResultsVectorWriter.writeResultsCsv(
                OUTPUT_NAME_FEATURES_AGGREGATED,
                map.entrySet(),
                metadata.groupHeaders(),
                featuresAggregate.createFeatureNames(),
                context,
                (name, results, csvWriter) ->
                        WriteGroupResults.maybeWriteAggregatedResultsForSingleGroup(
                                name,
                                results,
                                featureNamesNonAggregate,
                                featuresAggregate,
                                csvWriter,
                                contextGroups.get(name.map(MultiName::toString))));
    }

    @Override
    public void close() throws IOException {
        writer.ifPresent(FeatureCSVWriter::close);
    }
}
