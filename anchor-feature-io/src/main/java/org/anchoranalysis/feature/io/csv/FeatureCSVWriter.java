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

import org.anchoranalysis.core.name.MultiName;
import org.anchoranalysis.core.text.TypedValue;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.ResultsVectorCollection;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.csv.CSVWriter;

public class FeatureCSVWriter {	
	
	private CSVWriter writer;	// If null, it mean's the writer is not switched ON
	
	private FeatureCSVWriter( CSVWriter writer ) {
		this.writer = writer;
	}
	
	// Can return null
	public static FeatureCSVWriter create( String outputName, BoundOutputManagerRouteErrors outputManager, String[] firstHeaderNames, FeatureNameList featureNames ) throws AnchorIOException {
		
		List<String> allHeaders = new ArrayList<String>( Arrays.asList(firstHeaderNames) );
		allHeaders.addAll( featureNames.asList() );
		
		if (!outputManager.isOutputAllowed(outputName) || featureNames==null) {
			return new FeatureCSVWriter(null);
		}
				
		CSVWriter writer = CSVWriter.createFromOutputManager(outputName, outputManager.getDelegate());
		
		if (writer==null) {
			return null;
		}
		
		writer.writeHeaders(allHeaders);
		return new FeatureCSVWriter(writer);
	}
	
	public void addResultsVectorWithGroup( MultiName group, ResultsVector resultsFromFeatures, boolean includeID ) {

		
		assert(resultsFromFeatures!=null);
		
		if (writer==null) {
			return;
		}
		
		addRow(
			buildCsvRow(group, resultsFromFeatures, includeID)
		);
	}
	
	/** Directly adds a row without any ResultsVector */
	public void addRow( List<TypedValue> values ) {
		
		if (writer==null) {
			return;
		}
		
		writer.writeRow(values);
	}
	
	public void addResultsVectorWithGroup( MultiName group, ResultsVectorCollection resultsCollectionFromFeatures, boolean includeID ) {
		
		if (writer==null) {
			return;
		}
		
		for( ResultsVector rv : resultsCollectionFromFeatures ) {
			addResultsVectorWithGroup(group, rv, includeID);
		}
	}

	public void close() {
		
		if (writer==null) {
			return;
		}
		
		writer.close();
	}
	
	// group is ignored if null
	private static List<TypedValue> buildCsvRow( MultiName group, ResultsVector resultsFromFeatures, boolean includeID ) {
		
		List<TypedValue> csvRow = new ArrayList<>();
		
		if (includeID) {
			assert(resultsFromFeatures.getIdentifier()!=null);
			csvRow.add( new TypedValue(resultsFromFeatures.getIdentifier(), false) );
		}
		
		if (group!=null) {
			for( int i=0; i<group.numParts(); i++ ) {
				csvRow.add( new TypedValue(group.getPart(i)) );
			}
		}
		
		resultsFromFeatures.addToTypeValueCollection(csvRow, 10);
		return csvRow;
	}
}