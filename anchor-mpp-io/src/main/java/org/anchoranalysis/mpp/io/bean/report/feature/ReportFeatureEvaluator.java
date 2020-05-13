package org.anchoranalysis.mpp.io.bean.report.feature;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.image.feature.bean.evaluator.FeatureEvaluator;

public abstract class ReportFeatureEvaluator<T extends FeatureInput> extends ReportFeatureForSharedObjects {

	// START BEAN PROPERTIES	
	@BeanField
	private FeatureEvaluator<T> featureEvaluator;
	
	@BeanField
	private String title;
	// END BEAN PROPERTIES
		
	protected void init( MPPInitParams so, LogErrorReporter logger ) throws InitException {
		// Maybe we should duplicate the providers?
		featureEvaluator.initRecursive( so.getFeature(), logger );
	}
	
	protected FeatureCalculatorSingle<T> createAndStartSession() throws OperationFailedException {
		return featureEvaluator.createAndStartSession();
	}
	
	@Override
	public String genTitleStr() throws OperationFailedException {
		return title;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public FeatureEvaluator<T> getFeatureEvaluator() {
		return featureEvaluator;
	}

	public void setFeatureEvaluator(FeatureEvaluator<T> featureEvaluator) {
		this.featureEvaluator = featureEvaluator;
	}
}
