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
package org.anchoranalysis.feature.io.csv.metadata;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.feature.name.FeatureNameList;

/**
 * Needed non-result information to output a feature-values CSV file.
 *
 * <p>i.e. headers, the output-name etc.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public class FeatureCSVMetadata {

    /**
     * Create for a particular output-name.
     *
     * @param outputName the output-name.
     * @param nonFeatureHeaders the headers that don't describe features (positioned left-most).
     * @param featureNamesToConsume the names of the features that will be outputted (positioned
     *     after {@code nonFeatureHeaders}. This structure will be modified internally.
     */
    public FeatureCSVMetadata(
            String outputName, String[] nonFeatureHeaders, FeatureNameList featureNamesToConsume) {
        featureNamesToConsume.insertBeginning(nonFeatureHeaders);
        this.outputName = outputName;
        this.headers = featureNamesToConsume.asList();
    }

    /** The output name for the features CSV. */
    private final String outputName;

    /** Headers for the CSV file. */
    private final List<String> headers;
}
