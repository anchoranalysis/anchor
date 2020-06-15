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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.anchoranalysis.core.name.MultiName;
import org.anchoranalysis.core.text.TypedValue;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.calc.results.ResultsVectorCollection;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.csv.CSVWriter;

public class FeatureCSVWriter {	
	
	private CSVWriter writer;	// If null, it mean's the writer is not switched ON
	
	private FeatureCSVWriter( CSVWriter writer ) {
		this.writer = writer;
	}
	
	public static Optional<FeatureCSVWriter> create( String outputName, BoundOutputManagerRouteErrors outputManager, String[] firstHeaderNames, FeatureNameList featureNames ) throws AnchorIOException {
		
		List<String> allHeaders = new ArrayList<String>( Arrays.asList(firstHeaderNames) );
		allHeaders.addAll( featureNames.asList() );
		
		if (!outputManager.isOutputAllowed(outputName) || featureNames==null) {
			return Optional.of(
				new FeatureCSVWriter(null)
			);
		}
				
		Optional<CSVWriter> writer = CSVWriter.createFromOutputManager(outputName, outputManager.getDelegate());
		return writer.map( w-> {
			w.writeHeaders(allHeaders);
			return new FeatureCSVWriter(w);
		});
	}
	
	public void addResultsVector( MultiName name, ResultsVector resultsFromFeatures, boolean includeID ) {

		
		assert(resultsFromFeatures!=null);
		
		if (writer==null) {
			return;
		}
		
		addRow(
			buildCsvRow(name, resultsFromFeatures, includeID)
		);
	}
	
	/** Directly adds a row without any ResultsVector */
	public void addRow( List<TypedValue> values ) {
		
		if (writer==null) {
			return;
		}
		
		writer.writeRow(values);
	}
	
	public void addResultsVector( MultiName name, ResultsVectorCollection resultsCollectionFromFeatures, boolean includeID ) {
		
		if (writer==null) {
			return;
		}
		
		for( ResultsVector rv : resultsCollectionFromFeatures ) {
			assert(name!=null);
			assert(rv!=null);
			addResultsVector(
				name,
				rv,
				includeID
			);
		}
	}

	public void close() {
		
		if (writer==null) {
			return;
		}
		
		writer.close();
	}
	
	// group is ignored if null
	private static List<TypedValue> buildCsvRow( MultiName name, ResultsVector resultsFromFeatures, boolean includeID ) {
		
		assert( resultsFromFeatures!=null );
		
		List<TypedValue> csvRow = new ArrayList<>();
		
		if (includeID) {
			assert(resultsFromFeatures.getIdentifier()!=null);
			csvRow.add(
				new TypedValue(resultsFromFeatures.getIdentifier(), false)
			);
		}
		
		for( int i=0; i<name.numParts(); i++ ) {
			csvRow.add( new TypedValue(name.getPart(i)) );
		}
		
		resultsFromFeatures.addToTypeValueCollection(csvRow, 10);
		return csvRow;
	}
}