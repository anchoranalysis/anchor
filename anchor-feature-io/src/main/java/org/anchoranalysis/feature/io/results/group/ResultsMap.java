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
import org.anchoranalysis.core.functional.checked.CheckedBiConsumer;
import org.anchoranalysis.feature.io.name.MultiName;
import org.anchoranalysis.feature.io.results.LabelledResultsVector;
import org.anchoranalysis.feature.results.ResultsVectorList;

class ResultsMap {

    /**
     * A map which stores an aggregate structure for all entries (based on their unique names) and
     * also on an aggregation-key extracted from the name
     */
    private MapCreate<Optional<MultiName>, ResultsVectorList> map =
            new MapCreate<>(
                    ResultsVectorList::new, Comparators.emptiesFirst(Comparator.naturalOrder()));

    public synchronized void addResultsFor(LabelledResultsVector results) {
        // Place into the aggregate structure
        map.computeIfAbsent(results.getLabels().getGroup()).add(results.getResults());
    }

    /**
     * Iterate over each result the map, and apply an operation.
     *
     * @param <E> an exception that may be thrown by {code operation}.
     * @param operation the operation applied to each element in the map, passing the key and value
     *     as parameters.
     * @throws E if {@code operation} throws it.
     */
    public <E extends Exception> void iterateResults(
            CheckedBiConsumer<Optional<MultiName>, ResultsVectorList, E> operation) throws E {
        map.iterateEntries(operation);
    }

    /**
     * Whether the map is empty or not.
     *
     * @return true iff the map is empty.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }
}
