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


import java.io.IOException;
import java.nio.file.Path;

import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.collection.TreeMapCreate;
import org.anchoranalysis.core.error.AnchorNeverOccursException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.MultiName;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.calc.results.ResultsVectorCollection;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.list.NamedFeatureStore;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.resultsvectorcollection.FeatureInputResults;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;


public class GroupedResultsVectorCollection {

	/**
	 * Headers describing the first few non-feature columns outputted in the CSV (2-3 columns with group and ID information)
	 */
	private String[] groupHeaderNames;
	
	/**
	 * Headers describing the first few non-feature columns outputted in the CSV for aggregrate rep (2-3 columns with group and ID information)
	 */
	private String[] groupHeaderNamesAggregate;
	
	
	/**
	 * Include IDs
	 */
	private boolean includeID = false;
	
	/**
	 * A map between the name of groups, to a collection of results for each-group
	 */
	private TreeMapCreate<MultiName,ResultsVectorCollection,AnchorNeverOccursException> groupMap = new TreeMapCreate<>(
		() -> new ResultsVectorCollection()
	);
	
	/**
	 * This constructor will include two group names in the outputting CSV file, but NO id column
	 * 
	 * @param idName
	 * @param firstGroupHeaderName
	 * @param secondGroupHeaderName
	 */
	public GroupedResultsVectorCollection( String firstGroupHeaderName, String secondGroupHeaderName ) {
		super();
		groupHeaderNames = new String[]{firstGroupHeaderName,secondGroupHeaderName};
		groupHeaderNamesAggregate = new String[]{firstGroupHeaderName,secondGroupHeaderName};
	}
	
	/**
	 * This constructor will include an ID column in the outputting CSV files, as well as two group names
	 * However, for the aggregate report, no ID is included, as it makes no sense as an aggregate.
	 * 
	 * @param idName
	 * @param firstGroupHeaderName
	 * @param secondGroupHeaderName
	 */
	public GroupedResultsVectorCollection( String idName, String firstGroupHeaderName, String secondGroupHeaderName ) {
		super();
		groupHeaderNames = new String[]{idName, firstGroupHeaderName,secondGroupHeaderName};
		groupHeaderNamesAggregate = new String[]{firstGroupHeaderName,secondGroupHeaderName};
		includeID = true;
	}
	

	public ResultsVectorCollection getOrCreateNew(MultiName groupID)
			throws GetOperationFailedException {
		return groupMap.getOrCreateNew(groupID);
	}
	
	
	/**
	 * Writes the stored-results to CSV files.
	 * 
	 * Two CSV files are (depending on output settings) outputted:
	 *    csvAll.csv:    all the features in a single CSV file
	 *    csvAgg.csv:    aggregate-functions applied to each group of features (based upon their group identifier)
	 *    
	 * Additionally, the following files might be outputted for each group
	 *    csvGroup.csv:  		the features for a particular-group
	 *    paramsGroupAgg.xml	the aggregate-functions applied to this particular-group (in an XML format)
	 * 
	 * @param featureNamesNonAggregate	names of feature functions (non-aggregate)
	 * @param featuresAggregate			aggregate-features
	 * @param outputManager				the output-manager
	 * @param logErrorReporter			logging and error-reporting
	 * @throws IOException
	 */
	public void writeResultsForAllGroups(
		FeatureNameList featureNamesNonAggregate,			
		NamedFeatureStore<FeatureInputResults> featuresAggregate,
		BoundOutputManagerRouteErrors outputManager,
		LogErrorReporter logErrorReporter
	) throws AnchorIOException {
		
		FeatureCSVWriter csvWriterAll = FeatureCSVWriter.create("csvAll", outputManager, groupHeaderNames, featureNamesNonAggregate );
		FeatureCSVWriter csvWriterAggregate = featuresAggregate!=null ? FeatureCSVWriter.create("csvAgg", outputManager, groupHeaderNamesAggregate, featuresAggregate.createFeatureNames() ) : null;
		
		try {
			for( MultiName group : groupMap.keySet() ) {
				
				ResultsVectorCollection resultsVectorCollection = groupMap.get(group);
				
				if( resultsVectorCollection.size()==0) {
					continue;
				}
				
				writeResultsForSingleGroup(
					group,
					resultsVectorCollection,
					featureNamesNonAggregate,
					featuresAggregate,
					csvWriterAll,
					csvWriterAggregate,
					outputManager,
					logErrorReporter
				);
			}
		} finally {
			if (csvWriterAggregate!=null) {
				csvWriterAggregate.close();
			}
			if (csvWriterAll!=null) {
				csvWriterAll.close();
			}
		}			
	}
	
	
	private <T extends FeatureInput> void writeResultsForSingleGroup(
		MultiName group,
		ResultsVectorCollection resultsVectorCollection,
		FeatureNameList featureNames,
		NamedFeatureStore<FeatureInputResults> featuresAggregate,	// If null, we don't do any feature aggregation
		FeatureCSVWriter csvWriterAll,			// If null, disabled
		FeatureCSVWriter csvWriterAggregate,	// If null, disabled
		BoundOutputManagerRouteErrors outputManager,
		LogErrorReporter logErrorReporter
	) throws AnchorIOException {
		
		assert(group!=null);
		
		BoundOutputManagerRouteErrors outputManagerGroup = outputManager.resolveFolder(
			group.getUniqueName()
		);
		assert(resultsVectorCollection!=null);
				
		outputManagerGroup.getWriterCheckIfAllowed().write(
			"csvGroup",
			() -> {
				FeatureListCSVGeneratorHorizontal generator = new FeatureListCSVGeneratorHorizontal( "groupedFeatureResults", featureNames );
				generator.setIterableElement(resultsVectorCollection);
				return generator;
			}
		);
		
		// If we write single-feature-results to a CSV file in each group
		//writeCSVResultsVector( featureNames, resultsVectorCollection, outputManagerGroup);
		
		// If we write ALL single-feature-results to a single CSV file
		if (csvWriterAll!=null) {
			csvWriterAll.addResultsVectorWithGroup(group, resultsVectorCollection, includeID);
		}

		if (featuresAggregate!=null) {
			ResultsVector rv = createAggregateResultsVector(
				featuresAggregate,
				featureNames,
				resultsVectorCollection,
				logErrorReporter
			);
			
			// Write aggregate-feature-results to a KeyValueparams file
			writeKeyValueParams( featuresAggregate, rv, outputManagerGroup, logErrorReporter );

			if (csvWriterAggregate!=null) {
				assert(rv!=null);
				
				// If we write aggregate-feature-results to a single CSV file
				csvWriterAggregate.addResultsVectorWithGroup(group, rv, false);
			}
		}
	}
		
	private static ResultsVector createAggregateResultsVector(
		NamedFeatureStore<FeatureInputResults> featuresAggregate,
		FeatureNameList featureNamesSource,
		ResultsVectorCollection featuresCollection,
		LogErrorReporter logErrorReporter
	) throws AnchorIOException {
		
		FeatureCalculatorMulti<FeatureInputResults> session;
		
		try {
			session = FeatureSession.with(
				featuresAggregate.listFeatures(),
				logErrorReporter
			);
			
		} catch (FeatureCalcException e1) {
			logErrorReporter.getErrorReporter().recordError(GroupedResultsVectorCollection.class, e1);
			throw new AnchorIOException("Cannot start feature-session", e1);
		}
		
		FeatureInputResults params = new FeatureInputResults(
			featuresCollection,
			featureNamesSource.createMapToIndex()
		);
		
		return session.calcSuppressErrors(params, logErrorReporter.getErrorReporter() );
	}
	
	private static <T extends FeatureInput> void writeKeyValueParams(
			NamedFeatureStore<T> featuresAggregate,
			ResultsVector rv,
			BoundOutputManagerRouteErrors outputManager,
			LogErrorReporter logErrorReporter
	) {
		
		KeyValueParams paramsOut = new KeyValueParams();
		
		for( int i=0; i<featuresAggregate.size(); i++ ) {
			
			NamedBean<Feature<T>> item = featuresAggregate.get(i);
			
			double val = rv.get(i);
			paramsOut.put(item.getName(), Double.toString(val));
		}
		
		try {
			Path fileOutPath = outputManager.getWriterCheckIfAllowed().writeGenerateFilename("paramsGroupAgg", "xml", new ManifestDescription("paramsXML", "aggregateObjMask"), "", "", "");
			if( fileOutPath!=null ) {
				paramsOut.writeToFile(fileOutPath);
			}
		} catch (IOException | OutputWriteFailedException e) {
			logErrorReporter.getErrorReporter().recordError(GroupedResultsVectorCollection.class, e);
		}
	}	
}
