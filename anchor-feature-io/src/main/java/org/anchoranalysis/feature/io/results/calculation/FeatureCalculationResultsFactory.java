package org.anchoranalysis.feature.io.results.calculation;

import org.anchoranalysis.feature.io.results.FeatureOutputMetadata;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


/**
 * Creates {@link FeatureCalculationResults}.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class FeatureCalculationResultsFactory {

    /**
     * Creates with appropriate support classes for outputting.
     *
     * @param outputMetadata metadata needed for determining output-names and CSV headers.
     * @param writerCreator when true, columns containing all {@link Double#NaN} values are removed before outputting.
     * @param removeNaNColumns when true, columns containing all {@link Double#NaN} values are removed before outputting.
     * @return a newly created {@link FeatureCalculationResults}.
     * @throws OutputWriteFailedException if a CSV for (non-aggregated) features fails to be
     *     created.
     */
    public static FeatureCalculationResults create(FeatureOutputMetadata outputMetadata, FeatureCSVWriterCreator writerCreator, boolean removeNaNColumns) throws OutputWriteFailedException {
        if (removeNaNColumns) {
            return new RemoveNaNColumns(outputMetadata, writerCreator);
        } else {
            return new WriteEager(outputMetadata, writerCreator);
        }
    }
}
