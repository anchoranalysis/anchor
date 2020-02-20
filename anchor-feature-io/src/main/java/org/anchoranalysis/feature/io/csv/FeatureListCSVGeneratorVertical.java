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
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.ResultsVectorCollection;
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

	protected void writeRowsAndColumns( CSVWriter writer, ResultsVectorCollection featureValues, List<String> headerNames ) throws OutputWriteFailedException {
		
		for( int featureIndex=0; featureIndex<headerNames.size(); featureIndex++ ) {
			String featureName = headerNames.get(featureIndex);
			
			List<TypedValue> csvRow = new ArrayList<>();
			csvRow.add( new TypedValue( featureName ) );
			
			for( ResultsVector rv : featureValues ) {
				
				if (rv.length()!=headerNames.size()) {
					throw new OutputWriteFailedException(
						String.format("ResultsVector has size (%d) != featureNames vector (%d)", rv.length(), headerNames.size())
					);
				}
				
				double val = rv.get(featureIndex);
					
				if (Double.isNaN(val)) {
					csvRow.add( new TypedValue("Error") );
				} else {
					csvRow.add( new TypedValue(val, 10) );
				}					
			}
			
			writer.writeRow(csvRow);
		}
	}
}
