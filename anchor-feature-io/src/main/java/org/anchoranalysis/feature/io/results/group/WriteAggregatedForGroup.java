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
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.format.NonImageFileFormat;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorMulti;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.FeatureInputResults;
import org.anchoranalysis.feature.io.csv.results.LabelledResultsCSVWriter;
import org.anchoranalysis.feature.io.name.MultiName;
import org.anchoranalysis.feature.io.results.FeatureOutputMetadata;
import org.anchoranalysis.feature.io.results.LabelledResultsVector;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.results.ResultsVector;
import org.anchoranalysis.feature.results.ResultsVectorList;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.store.NamedFeatureStore;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.io.output.outputter.InputOutputContextSubdirectoryCache;

/**
 * Writes the aggregated results as XML to the filesystem.
 *
 * <p>The results are also added to a {@code csvWriterAggregate}, if it is defined.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class WriteAggregatedForGroup {

    private static final ManifestDescription MANIFEST_DESCRIPTION =
            new ManifestDescription("parametersXML", "aggregateObjects");

    private NamedFeatureStore<FeatureInputResults> featuresAggregate;
    private ResultsVectorList results;

    /**
     * Write the aggregated groups to the filesystem as XML, if enabled.
     *
     * @param groupName a group-name, if it exists.
     * @param metadata metadata for writing results to the filesystem.
     * @param aggegrateResults where aggregated-results are written to, if defined.
     * @param context a cached set of input-output contexts for directories for each group.
     * @throws OutputWriteFailedException if any writing fails
     */
    public void maybeWrite(
            Optional<MultiName> groupName,
            FeatureOutputMetadata metadata,
            Optional<LabelledResultsCSVWriter> aggegrateResults,
            InputOutputContextSubdirectoryCache context)
            throws OutputWriteFailedException {
        OptionalUtilities.ifPresent(
                metadata.outputNames().getXmlAggregatedGroup(),
                outputName ->
                        maybeWriteForOutput(
                                outputName,
                                groupName,
                                metadata.featureNamesNonAggregated(),
                                aggegrateResults,
                                context.get(groupName.map(MultiName::toString))));
    }

    private void maybeWriteForOutput(
            String outputName,
            Optional<MultiName> groupName,
            FeatureNameList featureNames,
            Optional<LabelledResultsCSVWriter> aggregateResults,
            InputOutputContext contextGroup)
            throws OutputWriteFailedException {
        if (aggregateResults.isPresent() || groupName.isPresent()) {
            ResultsVector aggregated = aggregateResults(featureNames, contextGroup.getLogger());

            // Write aggregate-feature-results to a parameters XML file
            if (groupName.isPresent()) {
                writeAggregatedAsParameters(
                        outputName, featuresAggregate, aggregated, contextGroup);
            }

            // Write the aggregated-features into the csv file
            aggregateResults.ifPresent(
                    writer -> writer.add(new LabelledResultsVector(groupName, aggregated)));
        }
    }

    /** Calculates an aggregate results vector */
    private ResultsVector aggregateResults(FeatureNameList featureNames, Logger logger)
            throws OutputWriteFailedException {

        FeatureCalculatorMulti<FeatureInputResults> calculator;

        try {
            calculator = FeatureSession.with(featuresAggregate.features(), logger);

        } catch (InitializeException e1) {
            logger.errorReporter().recordError(WriteAggregatedForGroup.class, e1);
            throw new OutputWriteFailedException("Cannot start feature-session", e1);
        }

        FeatureInputResults input =
                new FeatureInputResults(results, featureNames.createMapToIndex());

        return calculator.calculateSuppressErrors(input, logger.errorReporter());
    }

    private static <T extends FeatureInput> void writeAggregatedAsParameters(
            String outputName,
            NamedFeatureStore<T> featuresAggregate,
            ResultsVector results,
            InputOutputContext context) {

        Dictionary dictionary = new Dictionary();

        for (int i = 0; i < featuresAggregate.size(); i++) {

            NamedBean<Feature<T>> item = featuresAggregate.get(i);

            double val = results.get(i);
            dictionary.put(item.getName(), Double.toString(val));
        }

        try {
            Optional<Path> fileOutPath =
                    context.getOutputter()
                            .writerSelective()
                            .createFilenameForWriting(
                                    outputName,
                                    NonImageFileFormat.XML.extensionWithoutPeriod(),
                                    Optional.of(MANIFEST_DESCRIPTION));
            if (fileOutPath.isPresent()) {
                dictionary.writeToFile(fileOutPath.get());
            }
        } catch (IOException e) {
            context.getLogger().errorReporter().recordError(WriteAggregatedForGroup.class, e);
        }
    }
}
