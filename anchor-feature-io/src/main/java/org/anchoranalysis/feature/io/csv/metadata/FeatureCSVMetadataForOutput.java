/*-
 * #%L
 * anchor-feature-io
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.feature.io.csv.metadata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.anchoranalysis.feature.name.FeatureNameList;

/**
 * Like a {@link FeatureCSVMetadata} but additionally associates an output-name.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class FeatureCSVMetadataForOutput {

    /** Headers for the CSV File */
    @Getter private final String[] nonFeatureHeaders;

    /** Names of each feature in the feature columns. */
    @Getter private final FeatureNameList featureNames;

    /** Names for the produced output. */
    @Getter private final String outputName;

    /**
     * Derives metadata for writing to a CSV file
     *
     * @return newly created metadata
     */
    public FeatureCSVMetadata metadata() {
        return new FeatureCSVMetadata(
                outputName, nonFeatureHeaders, featureNames.duplicateShallow());
    }
}
