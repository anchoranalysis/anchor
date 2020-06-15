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
import java.util.Map.Entry;
import java.util.Optional;

import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.collection.TreeMapCreate;
import org.anchoranalysis.core.error.AnchorNeverOccursException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.MultiName;
import org.anchoranalysis.core.name.SimpleName;
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
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;


public class GroupedResultsVectorCollection {

	/**
	 * Headers describing the first few non-feature columns outputted in the CSV (2-3 columns with group and ID information)
	 */
	private String firstHeaderName;
	private String secondHeaderName;
	
	
	
	/**
	 * Include IDs
	 */
	private boolean includeID = false;
	private String idName;
	
	/**
	 * A map which stores all entries and a corresponding results-vector collection
	 */
	private TreeMapCreate<MultiName,ResultsVectorCollection,AnchorNeverOccursException> allMap = new TreeMapCreate<>(
		() -> new ResultsVectorCollection()
	);
	
	/**
	 * A map which stores a higher-level of grouping aggregation corresponding results-vector collection
	 */
	private TreeMapCreate<String,ResultsVectorCollection,AnchorNeverOccursException> aggregationMap = new TreeMapCreate<>(
		() -> new ResultsVectorCollection()
	);

	
	
	/**
	 * This constructor will include two group names in the outputting CSV file, but NO id column
	 * 
	 * @param idName
	 * @param firstHeaderName
	 * @param secondHeaderName
	 */
	public GroupedResultsVectorCollection( String firstHeaderName, String secondHeaderName ) {
		super();
		this.firstHeaderName = firstHeaderName;
		this.secondHeaderName = secondHeaderName;
	}
	
	/**
	 * This constructor will include an ID column in the outputting CSV files, as well as two group names
	 * However, for the aggregate report, no ID is included, as it makes no sense as an aggregate.
	 * 
	 * @param idName
	 * @param firstHeaderName
	 * @param secondHeaderName
	 */
	public GroupedResultsVectorCollection( String idName, String firstHeaderName, String secondHeaderName ) {
		super();
		this.firstHeaderName = firstHeaderName;
		this.secondHeaderName = secondHeaderName;
		this.idName = idName;
		includeID = true;
	}
	
	
	public void addResultsFor(MultiName identifier, ResultsVector rv) {
		allMap.getOrCreateNew(identifier).add(rv);
		aggregationMap.getOrCreateNew( identifier.getAggregateKeyName() ).add(rv);
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
	 * @param includeGroups 			iff TRUE a group-column is included in the CSV file and the group exports occur, otherwise not
	 * @param outputManager				the output-manager
	 * @param logErrorReporter			logging and error-reporting
	 * @throws IOException
	 */
	public void writeResultsForAllGroups(
		FeatureNameList featureNamesNonAggregate,			
		Optional<NamedFeatureStore<FeatureInputResults>> featuresAggregate,
		boolean includeGroups,
		BoundIOContext context
	) throws AnchorIOException {
		
		Optional<FeatureCSVWriter> csvWriterAll = FeatureCSVWriter.create(
			"csvAll",
			context.getOutputManager(),
			headerNames(includeID, includeGroups),
			featureNamesNonAggregate
		);
		
		
		try {
			for( Entry<MultiName,ResultsVectorCollection> entry : allMap.entrySet() ) {
				
				ResultsVectorCollection resultsVectorCollection = entry.getValue();
				
				if( resultsVectorCollection.size()==0) {
					continue;
				}
				
				writeResultsForSingleItem(
					entry.getKey(),
					resultsVectorCollection,
					csvWriterAll
				);
			}
		} finally {
			csvWriterAll.ifPresent( FeatureCSVWriter::close );
		}
		
		
		Optional<FeatureCSVWriter> csvWriterAggregate = OptionalUtilities.flatMap(
			featuresAggregate,
			fa -> FeatureCSVWriter.create(
				"csvAgg",
				context.getOutputManager(),
				new String[]{"groupAggregation"},	// TODO replace with more reliable group identifier
				fa.createFeatureNames()
			)
		);
		
		try {
			for( Entry<String,ResultsVectorCollection> entry : aggregationMap.entrySet() ) {
				
				ResultsVectorCollection resultsVectorCollection = entry.getValue();
				
				if( resultsVectorCollection.size()==0) {
					continue;
				}
				
				writeResultsForSingleGroup(
					entry.getKey(),
					resultsVectorCollection,
					featureNamesNonAggregate,
					featuresAggregate,
					csvWriterAggregate,
					context
				);
			}
		} finally {
			csvWriterAggregate.ifPresent( FeatureCSVWriter::close );
		}			
	}
	
	private <T extends FeatureInput> void writeResultsForSingleItem(
		MultiName name,
		ResultsVectorCollection resultsVectorCollection,
		Optional<FeatureCSVWriter> csvWriterAll
	) {
		// If we write ALL single-feature-results to a single CSV file
		if (csvWriterAll.isPresent()) {
			csvWriterAll.get().addResultsVector(name, resultsVectorCollection, includeID);
		}
	}
		
	private <T extends FeatureInput> void writeResultsForSingleGroup(
		String groupName,
		ResultsVectorCollection resultsVectorCollection,
		FeatureNameList featureNames,
		Optional<NamedFeatureStore<FeatureInputResults>> featuresAggregate,
		Optional<FeatureCSVWriter> csvWriterAggregate,
		BoundIOContext context
	) throws AnchorIOException {
		
		if (featuresAggregate.isPresent()) {
			
			BoundOutputManagerRouteErrors outputManagerGroup = context.getOutputManager().resolveFolder(groupName);
			assert(resultsVectorCollection!=null);
					
			outputManagerGroup.getWriterCheckIfAllowed().write(
				"csvGroup",
				() -> {
					FeatureListCSVGeneratorHorizontal generator = new FeatureListCSVGeneratorHorizontal( "groupedFeatureResults", featureNames );
					generator.setIterableElement(resultsVectorCollection);
					return generator;
				}
			);

			writeAggregateResultsForSingleGroup(
				groupName,
				resultsVectorCollection,
				featureNames,
				featuresAggregate.get(),
				csvWriterAggregate,
				outputManagerGroup,
				context.getLogger()
			);
		}
	}
	
	private void writeAggregateResultsForSingleGroup(
		String groupName,
		ResultsVectorCollection resultsVectorCollection,			
		FeatureNameList featureNames,
		NamedFeatureStore<FeatureInputResults> featuresAggregate,
		Optional<FeatureCSVWriter> csvWriterAggregate,
		BoundOutputManagerRouteErrors outputManagerGroup,
		LogErrorReporter logger
	) throws AnchorIOException {
		ResultsVector rv = createAggregateResultsVector(
			featuresAggregate,
			featureNames,
			resultsVectorCollection,
			logger
		);
		
		// Write aggregate-feature-results to a KeyValueparams file
		writeKeyValueParams( featuresAggregate, rv, outputManagerGroup, logger );

		if (csvWriterAggregate.isPresent()) {
			assert(rv!=null);
			
			// If we write aggregate-feature-results to a single CSV file
			csvWriterAggregate.get().addResultsVector(
				new SimpleName(groupName),
				rv,
				false
			);
		}
	}
	
	private String[] headerNames(boolean withIdColumn, boolean includeGroups) {
		if (withIdColumn) {
			return new String[]{idName, firstHeaderName,secondHeaderName};
		} else {
			return new String[]{firstHeaderName,secondHeaderName};
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
			Optional<Path> fileOutPath = outputManager.getWriterCheckIfAllowed().writeGenerateFilename(
				"paramsGroupAgg",
				"xml",
				Optional.of(
					new ManifestDescription("paramsXML", "aggregateObjMask")
				),
				"",
				"",
				""
			);
			if(fileOutPath.isPresent()) {
				paramsOut.writeToFile(fileOutPath.get());
			}
		} catch (IOException | OutputWriteFailedException e) {
			logErrorReporter.getErrorReporter().recordError(GroupedResultsVectorCollection.class, e);
		}
	}	
}
