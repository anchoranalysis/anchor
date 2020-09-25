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
import lombok.AllArgsConstructor;
import org.anchoranalysis.feature.io.csv.FeatureListCSVGeneratorHorizontal;
import org.anchoranalysis.feature.io.name.MultiName;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.results.ResultsVectorList;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.bound.CacheSubdirectoryContext;

/**
 * Writes the aggregated results for a single group as CSV to the filesystem.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class WriteCSVForGroup {

    private static final String MANIFEST_FUNCTION_FEATURES_GROUP = "groupedFeatureResults";

    private String outputName;
    private FeatureNameList featureNames;
    private CacheSubdirectoryContext context;

    public void write(Optional<MultiName> groupName, ResultsVectorList results) {
        if (groupName.isPresent()) {
            writeGroupFeatures(
                    context.get(groupName.map(MultiName::toString)).getOutputManager(), results);
        }
    }

    /** Writes a table of features in CSV for a particular group */
    private void writeGroupFeatures(
            BoundOutputManagerRouteErrors outputManager, ResultsVectorList results) {
        outputManager
                .getWriterCheckIfAllowed()
                .write(
                        outputName,
                        () ->
                                new FeatureListCSVGeneratorHorizontal(
                                        MANIFEST_FUNCTION_FEATURES_GROUP, featureNames, results));
    }
}
