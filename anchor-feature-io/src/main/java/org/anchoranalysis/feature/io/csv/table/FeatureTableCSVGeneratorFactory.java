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
package org.anchoranalysis.feature.io.csv.table;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.results.ResultsVectorList;

/**
 * Creates a {@link FeatureTableCSVGenerator}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeatureTableCSVGeneratorFactory {

    /**
     * Creates either a {@link FeatureTableCSVGenerator} in either horizontal or vertical style.
     *
     * @param manifestFunction identifier of function for the manifest file.
     * @param featureNames names-of-features that will appear in results.
     * @param horizontal if true, horizontal style is used (an entity's results form a row),
     *     otherwise vertical style (an entity's results form a column).
     * @return a newly created {@link FeatureTableCSVGenerator} in the above selected style.
     */
    public static FeatureTableCSVGenerator<ResultsVectorList> create(
            String manifestFunction, FeatureNameList featureNames, boolean horizontal) {
        if (horizontal) {
            return new HorizontalValues(manifestFunction, featureNames);
        } else {
            return new VerticalValues(manifestFunction, featureNames);
        }
    }
}
