/* (C)2020 */
package org.anchoranalysis.feature.io.csv;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.feature.calc.results.ResultsVectorCollection;
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
