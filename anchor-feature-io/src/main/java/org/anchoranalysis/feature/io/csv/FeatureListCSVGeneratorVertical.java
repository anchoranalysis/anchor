package org.anchoranalysis.feature.io.csv;

/*
 * #%L
 * anchor-feature-io
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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
 *
 */
public class FeatureListCSVGeneratorVertical extends TableCSVGenerator<ResultsVectorCollection> {

	public FeatureListCSVGeneratorVertical(String manifestFunction,
			FeatureNameList featureNames) {
		super(manifestFunction, featureNames.asList() );
	}

	@Override
	protected void writeRowsAndColumns( CSVWriter writer, ResultsVectorCollection featureValues, List<String> headerNames ) throws OutputWriteFailedException {
		
		int size = headerNames.size();
		
		for( int featureIndex=0; featureIndex<size; featureIndex++ ) {
			String featureName = headerNames.get(featureIndex);
			
			writer.writeRow(
				generateRow(featureName, featureValues, featureIndex, size)
			);
		}
	}
	
	private static List<TypedValue> generateRow( String featureName, ResultsVectorCollection featureValues, int featureIndex, int size ) throws OutputWriteFailedException {

		List<TypedValue> csvRow = new ArrayList<>();
		
		// The Name
		csvRow.add(
			new TypedValue( featureName )
		);
		
		for( ResultsVector rv : featureValues ) {
			
			if (rv.length()!=size) {
				throw new OutputWriteFailedException(
					String.format("ResultsVector has size (%d) != featureNames vector (%d)", rv.length(), size)
				);
			}

			csvRow.add(
				replaceNaN( rv.get(featureIndex) )
			);
		}

		return csvRow;
	}
	
	/** Replaces NaN with error */
	private static TypedValue replaceNaN( double val ) {
		if (Double.isNaN(val)) {
			return new TypedValue("Error");
		} else {
			return new TypedValue(val, 10);
		}
	}
}
