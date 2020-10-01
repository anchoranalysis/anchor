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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.FeatureInputResults;
import org.anchoranalysis.feature.io.csv.FeatureCSVWriter;
import org.anchoranalysis.feature.io.csv.RowLabels;
import org.anchoranalysis.feature.io.name.MultiName;
import org.anchoranalysis.feature.io.results.ResultsWriter;
import org.anchoranalysis.feature.io.results.ResultsWriterMetadata;
import org.anchoranalysis.feature.list.NamedFeatureStore;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.results.ResultsVector;
import org.anchoranalysis.feature.results.ResultsVectorList;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.io.output.outputter.InputOutputContextSubdirectoryCache;

/**
 * Writes the aggregated results for a single group as XML to the filesystem.
 *
 * <p>The results are also added to a {@code csvWriterAggregate} if it is defined.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class WriteXMLForGroup {

    private static final ManifestDescription MANIFEST_DESCRIPTION =
            new ManifestDescription("paramsXML", "aggregateObjects");

    private NamedFeatureStore<FeatureInputResults> featuresAggregate;
    private ResultsVectorList results;

    /**
     * Write the aggregated groups to the filesystem as XML, if enabled.
     *
     * @param groupName a group-name, if it exists.
     * @param metadata metadata for writing results to the filesystem.
     * @param csvWriter a CSV-writer, if it's enabled.
     * @param context a cached set of input-output contexts for directories for each group.
     * @throws AnchorIOException if an input-output problem occurs.
     */
    public void maybeWrite(
            Optional<MultiName> groupName,
            ResultsWriterMetadata metadata,
            Optional<FeatureCSVWriter> csvWriter,
            InputOutputContextSubdirectoryCache context)
            throws AnchorIOException {
        OptionalUtilities.ifPresent(
                metadata.outputNames().getXmlAggregatedGroup(),
                outputName ->
                        maybeWriteForOutput(
                                outputName,
                                groupName,
                                metadata.featureNamesNonAggregate(),
                                csvWriter,
                                context.get(groupName.map(MultiName::toString))));
    }

    private void maybeWriteForOutput(
            String outputName,
            Optional<MultiName> groupName,
            FeatureNameList featureNames,
            Optional<FeatureCSVWriter> csvWriterAggregate,
            InputOutputContext contextGroup)
            throws AnchorIOException {
        if (csvWriterAggregate.isPresent() || groupName.isPresent()) {
            ResultsVector aggregated = aggregateResults(featureNames, contextGroup.getLogger());

            // Write aggregate-feature-results to a params XML file
            if (groupName.isPresent()) {
                writeAggregatedAsParams(outputName, featuresAggregate, aggregated, contextGroup);
            }

            // Write the aggregated-features into the csv file
            csvWriterAggregate.ifPresent(
                    writer ->
                            writer.addRow(new RowLabels(Optional.empty(), groupName), aggregated));
        }
    }

    /** Calculates an aggregate results vector */
    private ResultsVector aggregateResults(FeatureNameList featureNames, Logger logger)
            throws AnchorIOException {

        FeatureCalculatorMulti<FeatureInputResults> calculator;

        try {
            calculator = FeatureSession.with(featuresAggregate.listFeatures(), logger);

        } catch (InitException e1) {
            logger.errorReporter().recordError(ResultsWriter.class, e1);
            throw new AnchorIOException("Cannot start feature-session", e1);
        }

        FeatureInputResults input =
                new FeatureInputResults(results, featureNames.createMapToIndex());

        return calculator.calculateSuppressErrors(input, logger.errorReporter());
    }

    private static <T extends FeatureInput> void writeAggregatedAsParams(
            String outputName,
            NamedFeatureStore<T> featuresAggregate,
            ResultsVector results,
            InputOutputContext context) {

        KeyValueParams paramsOut = new KeyValueParams();

        for (int i = 0; i < featuresAggregate.size(); i++) {

            NamedBean<Feature<T>> item = featuresAggregate.get(i);

            double val = results.get(i);
            paramsOut.put(item.getName(), Double.toString(val));
        }

        try {
            Optional<Path> fileOutPath =
                    context.getOutputter()
                            .writerSelective()
                            .writeGenerateFilename(
                                    outputName, "xml", Optional.of(MANIFEST_DESCRIPTION));
            if (fileOutPath.isPresent()) {
                paramsOut.writeToFile(fileOutPath.get());
            }
        } catch (IOException e) {
            context.getLogger().errorReporter().recordError(ResultsWriter.class, e);
        }
    }
}
