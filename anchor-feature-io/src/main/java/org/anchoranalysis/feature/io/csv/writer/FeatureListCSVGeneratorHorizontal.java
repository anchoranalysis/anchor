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
 * CSV file where each Feature is a column (spanning horizontally)
 *
 * @author Owen Feehan
 */
public class FeatureListCSVGeneratorHorizontal extends TableCSVGenerator<ResultsVectorCollection> {

    public FeatureListCSVGeneratorHorizontal(
            String manifestFunction, FeatureNameList featureNames) {
        super(manifestFunction, featureNames.asList());
    }

    public FeatureListCSVGeneratorHorizontal(
            String manifestFunction,
            FeatureNameList featureNames,
            ResultsVectorCollection results) {
        this(manifestFunction, featureNames);
        setIterableElement(results);
    }

    @Override
    protected void writeRowsAndColumns(
            CSVWriter writer, ResultsVectorCollection featureValues, List<String> headerNames)
            throws OutputWriteFailedException {

        // We add a header line
        writer.writeHeaders(headerNames);

        for (ResultsVector rv : featureValues) {

            List<TypedValue> csvRow = new ArrayList<>();
            rv.addToTypeValueCollection(csvRow, 10);
            writer.writeRow(csvRow);
        }
    }
}
