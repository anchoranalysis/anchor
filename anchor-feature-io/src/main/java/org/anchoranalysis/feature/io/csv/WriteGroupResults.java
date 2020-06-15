package org.anchoranalysis.feature.io.csv;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.SimpleName;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.calc.results.ResultsVectorCollection;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.io.csv.writer.FeatureCSVWriter;
import org.anchoranalysis.feature.io.csv.writer.FeatureListCSVGeneratorHorizontal;
import org.anchoranalysis.feature.list.NamedFeatureStore;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.resultsvectorcollection.FeatureInputResults;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.bound.CacheSubdirectoryContext;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Writing the group or aggregated related results to the filesystem
 * 
 * @author Owen Feehan
 *
 */
class WriteGroupResults {

	/** The name of the CSV file outputted with feature-values for each group */
	private final static String OUTPUT_NAME_FEATURES_GROUP = "featuresGroup";
	
	/** The name of the XML file outputted with aggregated values for each group */
	private final static String OUTPUT_NAME_PARAMS = "featuresAggregatedGroup";
	
	private final static String MANIFEST_FUNCTION_FEATURES_GROUP = "groupedFeatureResults";
	
	private final static ManifestDescription MANIFEST_PARAMS = new ManifestDescription("paramsXML", "aggregateObjMask");
	
	private WriteGroupResults() {}
	
	public static <T extends FeatureInput> void writeResultsForSingleGroup(
		Optional<String> groupName,
		ResultsVectorCollection results,
		FeatureNameList featureNames,
		CacheSubdirectoryContext context
	) {
		if (groupName.isPresent()) {
			writeGroupFeatures(
				context.get(groupName).getOutputManager(),
				results,
				featureNames
			);
		}
	}
		
	public static <T extends FeatureInput> void maybeWriteAggregatedResultsForSingleGroup(
		Optional<String> groupName,
		ResultsVectorCollection results,
		FeatureNameList featureNames,
		NamedFeatureStore<FeatureInputResults> featuresAggregate,
		Optional<FeatureCSVWriter> csvWriterAggregate,
		BoundIOContext contextGroup
	) throws AnchorIOException {

		assert(results!=null);
				
		if (csvWriterAggregate.isPresent() || groupName.isPresent()) {
			writeAggregateResultsForSingleGroup(
				groupName,
				results,
				featureNames,
				featuresAggregate,
				csvWriterAggregate,
				contextGroup
			);
		}
	}
	
	/** Writes a table of features in CSV for a particular group */
	private static void writeGroupFeatures(
		BoundOutputManagerRouteErrors outputManager,
		ResultsVectorCollection results,
		FeatureNameList featureNames
	) {
		outputManager.getWriterCheckIfAllowed().write(
			OUTPUT_NAME_FEATURES_GROUP,
			() -> new FeatureListCSVGeneratorHorizontal(
				MANIFEST_FUNCTION_FEATURES_GROUP,
				featureNames,
				results
			)
		);
	}
	
	private static void writeAggregateResultsForSingleGroup(
		Optional<String> groupName,
		ResultsVectorCollection resultsVectorCollection,			
		FeatureNameList featureNames,
		NamedFeatureStore<FeatureInputResults> featuresAggregate,
		Optional<FeatureCSVWriter> csvWriterAggregate,
		BoundIOContext contextGroup
	) throws AnchorIOException {
		
		ResultsVector rv = createAggregateResultsVector(
			featuresAggregate,
			featureNames,
			resultsVectorCollection,
			contextGroup.getLogger()
		);
		
		// Write aggregate-feature-results to a params XML file
		if (groupName.isPresent()) {
			writeAggregatedAsParams( featuresAggregate, rv, contextGroup );
		}

		// Write the aggregated-features into the csv file
		if (csvWriterAggregate.isPresent()) {
			assert(rv!=null);
			
			// If we write aggregate-feature-results to a single CSV file
			csvWriterAggregate.get().addResultsVector(
				groupName.map(
					name-> new SimpleName(name)
				),
				rv,
				false
			);
		}
	}
	
	/** Calculates an aggregate results vector */
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
	
	private static <T extends FeatureInput> void writeAggregatedAsParams(
		NamedFeatureStore<T> featuresAggregate,
		ResultsVector rv,
		BoundIOContext context
	) {
		
		KeyValueParams paramsOut = new KeyValueParams();
		
		for( int i=0; i<featuresAggregate.size(); i++ ) {
			
			NamedBean<Feature<T>> item = featuresAggregate.get(i);
			
			double val = rv.get(i);
			paramsOut.put(item.getName(), Double.toString(val));
		}
		
		try {
			Optional<Path> fileOutPath = context.getOutputManager().getWriterCheckIfAllowed().writeGenerateFilename(
				OUTPUT_NAME_PARAMS,
				"xml",
				Optional.of(MANIFEST_PARAMS)
			);
			if(fileOutPath.isPresent()) {
				paramsOut.writeToFile(fileOutPath.get());
			}
		} catch (IOException | OutputWriteFailedException e) {
			context.getLogger().getErrorReporter().recordError(GroupedResultsVectorCollection.class, e);
		}
	}
}
