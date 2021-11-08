package org.anchoranalysis.feature.io.results;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.feature.io.csv.RowLabels;
import org.anchoranalysis.feature.io.name.MultiName;
import org.anchoranalysis.feature.results.ResultsVector;

/**
 * Like a {@link ResultsVector} but additionally contains labels to describe the calculated results.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public class LabelledResultsVector {

    /** The labels. */
    private final RowLabels labels;

    /** The results. */
    private final ResultsVector results;

    /**
     * Creates with no additional labels, and no group.
     *
     * @param results the results.
     */
    public LabelledResultsVector(ResultsVector results) {
        this(Optional.empty(), results);
    }

    /**
     * Creates with no additional labels other than a group.
     *
     * @param group the associated group.
     * @param results the results.
     */
    public LabelledResultsVector(Optional<MultiName> group, ResultsVector results) {
        this(new RowLabels(Optional.empty(), group), results);
    }

    /**
     * The result of a feature-calculation stored at a particular {@code index}.
     *
     * @param index the index (zero-indexed). It should be {@code >= 0} and {@code < size()}.
     * @return the value corresponding to the feature-calculation or {@link Double#NaN} if an
     *     exception occurred during calculation.
     */
    public double get(int index) {
        return results.get(index);
    }

    /**
     * The number of calculations stored in the vector.
     *
     * @return the total number of calculations in the vector.
     */
    public int size() {
        return results.size();
    }
}
