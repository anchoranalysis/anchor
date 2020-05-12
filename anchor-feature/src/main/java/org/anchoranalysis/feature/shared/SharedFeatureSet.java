package org.anchoranalysis.feature.shared;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.value.INameValue;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;

public class SharedFeatureSet<T extends FeatureInput> {
	
	private NameValueSet<Feature<T>> set;
	
	public SharedFeatureSet(NameValueSet<Feature<T>> set) {
		super();
		this.set = set;
	}
	
	// Uses names of features
	/*public void addDuplicate( FeatureList<T> features ) {
		for( Feature<T> f : features ) {
			set.add( f.getFriendlyName(), f.duplicateBean() );
		}
	}*/

	public void initRecursive( FeatureInitParams featureInitParams, LogErrorReporter logger ) throws InitException {
		for( INameValue<Feature<T>> nv : set ) {
			nv.getValue().initRecursive( featureInitParams, logger);
		}
	}
	
	public NameValueSet<Feature<T>> getSet() {
		return set;
	}

	public Feature<T> getException(String name) throws NamedProviderGetException {
		return set.getException(name);
	}
}
