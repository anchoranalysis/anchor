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

import com.google.common.collect.Comparators;
import java.util.Comparator;
import java.util.Optional;
import org.anchoranalysis.core.collection.MapCreate;
import org.anchoranalysis.core.functional.FunctionalIterate;
import org.anchoranalysis.core.functional.function.CheckedBiConsumer;
import org.anchoranalysis.feature.io.csv.RowLabels;
import org.anchoranalysis.feature.io.name.MultiName;
import org.anchoranalysis.feature.results.ResultsVector;
import org.anchoranalysis.feature.results.ResultsVectorList;

class ResultsMap {

    /**
     * A map which stores an aggregate structure for all entries (based on their unique names) and
     * also on an aggregation-key extracted from the name
     */
    private MapCreate<Optional<MultiName>, ResultsVectorList> map =
            new MapCreate<>(
                    ResultsVectorList::new, Comparators.emptiesFirst(Comparator.naturalOrder()));

    public void addResultsFor(RowLabels labels, ResultsVector results) {
        // Place into the aggregate structure
        map.getOrCreateNew(labels.getGroup()).add(results);
    }

    public <E extends Exception> void iterateResults(
            CheckedBiConsumer<Optional<MultiName>, ResultsVectorList, E> consumer) throws E {
        FunctionalIterate.iterateMap(map.asMap(), consumer);
    }

    public boolean isEmpty() {
        return map.entrySet().isEmpty();
    }
}
