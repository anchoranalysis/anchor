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

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.feature.calculate.results.ResultsVectorCollection;
import org.anchoranalysis.feature.io.csv.writer.FeatureCSVWriter;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.output.bound.BoundIOContext;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ResultsVectorWriter {

    /** Called on each results entry */
    public interface ProcessResultsEntry<T> {
        void process(T name, ResultsVectorCollection results, Optional<FeatureCSVWriter> writer)
                throws AnchorIOException;
    }

    public static <T> void writeResultsCsv(
            String outputName,
            Collection<Entry<T, ResultsVectorCollection>> entries,
            String[] headers,
            FeatureNameList featureNames,
            BoundIOContext context,
            ProcessResultsEntry<T> processEntry)
            throws AnchorIOException {

        if (entries.isEmpty()) {
            // NOTHING TO DO, exit early
            return;
        }

        Optional<FeatureCSVWriter> writer =
                FeatureCSVWriter.create(
                        outputName, context.getOutputManager(), headers, featureNames);

        try {
            for (Entry<T, ResultsVectorCollection> entry : entries) {

                ResultsVectorCollection resultsVectorCollection = entry.getValue();

                if (resultsVectorCollection.size() == 0) {
                    continue;
                }

                processEntry.process(entry.getKey(), resultsVectorCollection, writer);
            }
        } finally {
            writer.ifPresent(FeatureCSVWriter::close);
        }
    }
}
