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
import java.util.Map.Entry;
import java.util.Optional;

import org.anchoranalysis.core.name.MultiName;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.calc.results.ResultsVectorCollection;
import org.anchoranalysis.feature.list.NamedFeatureStore;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.resultsvectorcollection.FeatureInputResults;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.CacheSubdirectoryContext;
import org.apache.commons.math3.util.Pair;


public class GroupedResultsVectorCollection {

	private final static String OUTPUT_NAME_FEATURES = "features";
	private final static String OUTPUT_NAME_FEATURES_AGGREGATED = "featuresAggregated";
	
	/**
	 * Headers describing the first few non-feature columns outputted in the CSV (2-3 columns with group and ID information)
	 */
	private String groupHeader;
	private String identifierHeader;
	
	
	
	/**
	 * Include IDs
	 */
	private boolean includeID = false;
	private String idName;
	
	/**
	 * A map which stores an aggregate structure for all entries (based on their unique names) and also on an aggregation-key extracted from the name
	 */
	private AggregateMap<MultiName,ResultsVectorCollection> map = new AggregateMap<>(
		() -> new ResultsVectorCollection()
	);
	
	/**
	 * This constructor will include two group names in the outputting CSV file, but NO id column
	 * 
	 * @param idName
	 * @param groupHeader
	 * @param identifierHeader
	 */
	public GroupedResultsVectorCollection( String groupHeader, String identifierHeader ) {
		super();
		this.groupHeader = groupHeader;
		this.identifierHeader = identifierHeader;
	}
	
	/**
	 * This constructor will include an ID column in the outputting CSV files, as well as two group names
	 * However, for the aggregate report, no ID is included, as it makes no sense as an aggregate.
	 * 
	 * @param idName
	 * @param groupHeader
	 * @param identifierHeader
	 */
	public GroupedResultsVectorCollection( String idName, String groupHeader, String identifierHeader ) {
		super();
		this.groupHeader = groupHeader;
		this.identifierHeader = identifierHeader;
		this.idName = idName;
		includeID = true;
	}
	
	
	public void addResultsFor(MultiName identifier, ResultsVector rv) {
		
		Pair<ResultsVectorCollection,ResultsVectorCollection> pair = map.getOrCreate(identifier);
		pair.getFirst().add(rv);
		pair.getSecond().add(rv);
	}
	
	/**
	 * Writes the stored-results to CSV files.
	 * 
	 * <pre>
	 * Two CSV files are (depending on output settings) outputted:
	 *    features.csv:    			 all the features in a single CSV file
	 *    featuresAggregated.csv:    aggregate-functions applied to each group of features (based upon their group identifier)
	 *    
	 * Additionally, the following files might be outputted for each group
	 *    featuresGroup.csv:  			the features for a particular-group
	 *    featuresAggregatedGroup.xml	the aggregate-functions applied to this particular-group (in an XML format)
	 * </pre>
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
				
		writeByName(featureNamesNonAggregate, includeGroups, context);
		
		CacheSubdirectoryContext contextGroups = new CacheSubdirectoryContext(
			context.subdirectory("grouped")
		);
		
		if (includeGroups) {
			writeAllGroups(featureNamesNonAggregate, contextGroups);
		}
		
		if (featuresAggregate.isPresent()) {
			writeAggregated(
				featuresAggregate.get(),
				featureNamesNonAggregate,
				includeGroups,
				context,
				contextGroups
			);
		}
	}
	
	private void writeByName(
		FeatureNameList featureNames,
		boolean includeGroups,
		BoundIOContext context
	) throws AnchorIOException {
			
		ResultsVectorWriter.writeResultsCsv(
			OUTPUT_NAME_FEATURES,
			map.byEntireName(),
			headerNames(includeID, includeGroups),
			featureNames,
			context,
			(name, results, csvWriter) -> csvWriter.ifPresent( writer->
				writer.addResultsVector(
					Optional.of(name),
					results,
					includeID
				)
			)
		);
	}
	
	private void writeAllGroups( FeatureNameList featureNames, CacheSubdirectoryContext context ) {
		for( Entry<Optional<String>,ResultsVectorCollection> entry : map.byAggregationKeys() ) {
			WriteGroupResults.writeResultsForSingleGroup(
				entry.getKey(),
				entry.getValue(),
				featureNames,
				context
			);
		}
	}
	
	private void writeAggregated(
		NamedFeatureStore<FeatureInputResults> featuresAggregate,
		FeatureNameList featureNamesNonAggregate,
		boolean includeGroups,
		BoundIOContext context,
		CacheSubdirectoryContext contextGroups
	) throws AnchorIOException {
			
		ResultsVectorWriter.writeResultsCsv(
			OUTPUT_NAME_FEATURES_AGGREGATED,
			map.byAggregationKeys(),
			includeGroups ? new String[]{groupHeader} : new String[]{},
			featuresAggregate.createFeatureNames(),
			context,
			(name, results, writer) -> WriteGroupResults.maybeWriteAggregatedResultsForSingleGroup(
				name,
				results,
				featureNamesNonAggregate,
				featuresAggregate,
				writer,
				contextGroups.get(name)
			)
		);
	}
		
	private String[] headerNames(boolean withIdColumn, boolean includeGroups) {
		if (withIdColumn) {
			if (includeGroups) {
				return new String[]{idName, groupHeader,identifierHeader};
			} else {
				return new String[]{idName, identifierHeader};
			}
		} else {
			if (includeGroups) {
				return new String[]{groupHeader,identifierHeader};
			} else {
				return new String[]{identifierHeader};
			}
		}
	}
}
