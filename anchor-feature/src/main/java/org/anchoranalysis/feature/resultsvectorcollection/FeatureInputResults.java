package org.anchoranalysis.feature.resultsvectorcollection;

import org.anchoranalysis.feature.calc.results.ResultsVectorCollection;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.name.FeatureNameMapToIndex;

public class FeatureInputResults extends FeatureInput {

	private ResultsVectorCollection resultsVectorCollection;
	private FeatureNameMapToIndex featureNameIndex;
	
	public FeatureInputResults(
			ResultsVectorCollection resultsVectorCollection,
			FeatureNameMapToIndex featureIndex) {
		super();
		this.resultsVectorCollection = resultsVectorCollection;
		this.featureNameIndex = featureIndex;
	}

	public ResultsVectorCollection getResultsVectorCollection() {
		return resultsVectorCollection;
	}

	public FeatureNameMapToIndex getFeatureNameIndex() {
		return featureNameIndex;
	}
	
}
