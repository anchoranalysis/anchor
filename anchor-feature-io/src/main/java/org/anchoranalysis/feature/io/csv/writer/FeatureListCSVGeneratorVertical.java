/* (C)2020 */
package org.anchoranalysis.feature.io.csv.writer;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.core.text.TypedValue;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.calc.results.ResultsVectorCollection;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.io.output.csv.CSVWriter;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * CSV file where each Feature is a row (spanning vertically)
 *
 * @author Owen Feehan
 */
public class FeatureListCSVGeneratorVertical extends TableCSVGenerator<ResultsVectorCollection> {

    public FeatureListCSVGeneratorVertical(String manifestFunction, FeatureNameList featureNames) {
        super(manifestFunction, featureNames.asList());
    }

    @Override
    protected void writeRowsAndColumns(
            CSVWriter writer, ResultsVectorCollection featureValues, List<String> headerNames)
            throws OutputWriteFailedException {

        int size = headerNames.size();

        for (int featureIndex = 0; featureIndex < size; featureIndex++) {
            String featureName = headerNames.get(featureIndex);

            writer.writeRow(generateRow(featureName, featureValues, featureIndex, size));
        }
    }

    private static List<TypedValue> generateRow(
            String featureName, ResultsVectorCollection featureValues, int featureIndex, int size)
            throws OutputWriteFailedException {

        List<TypedValue> csvRow = new ArrayList<>();

        // The Name
        csvRow.add(new TypedValue(featureName));

        for (ResultsVector rv : featureValues) {

            if (rv.length() != size) {
                throw new OutputWriteFailedException(
                        String.format(
                                "ResultsVector has size (%d) != featureNames vector (%d)",
                                rv.length(), size));
            }

            csvRow.add(replaceNaN(rv.get(featureIndex)));
        }

        return csvRow;
    }

    /** Replaces NaN with error */
    private static TypedValue replaceNaN(double val) {
        if (Double.isNaN(val)) {
            return new TypedValue("Error");
        } else {
            return new TypedValue(val, 10);
        }
    }
}
