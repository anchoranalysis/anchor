package org.anchoranalysis.feature.io.csv.results.group;

import java.util.Comparator;
import java.util.Optional;
import org.anchoranalysis.core.collection.MapCreate;
import org.anchoranalysis.core.functional.FunctionalIterate;
import org.anchoranalysis.core.functional.function.CheckedBiConsumer;
import org.anchoranalysis.feature.calculate.results.ResultsVector;
import org.anchoranalysis.feature.calculate.results.ResultsVectorCollection;
import org.anchoranalysis.feature.io.csv.name.MultiName;
import org.anchoranalysis.feature.io.csv.writer.RowLabels;
import com.google.common.collect.Comparators;

class ResultsMap {

    /**
     * A map which stores an aggregate structure for all entries (based on their unique names) and
     * also on an aggregation-key extracted from the name
     */
    private MapCreate<Optional<MultiName>, ResultsVectorCollection> map =
            new MapCreate<>(
                    ResultsVectorCollection::new,
                    Comparators.emptiesFirst(Comparator.naturalOrder()));
    
    public void addResultsFor(RowLabels labels, ResultsVector results) {
        // Place into the aggregate structure
        map.getOrCreateNew(labels.getGroup()).add(results);
    }
    
    public <E extends Exception> void iterateResults( CheckedBiConsumer<Optional<MultiName>, ResultsVectorCollection, E> consumer ) throws E {
        FunctionalIterate.iterateMap(map.asMap(), consumer);
    }
    
    public boolean isEmpty() {
        return map.entrySet().isEmpty();
    }
}
