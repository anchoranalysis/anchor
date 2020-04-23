package org.anchoranalysis.feature.list;

/*-
 * #%L
 * anchor-feature
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.params.FeatureInput;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

public class FeatureListStoreUtilities {
	
	public static <T extends FeatureInput> void addFeatureListToStoreNoDuplicateDirectly( INamedProvider<FeatureList<T>> featureListProvider, SharedFeatureSet<T> out ) {
		
		for( String key : featureListProvider.keys()) {
			try {
				FeatureList<T> fl = featureListProvider.getException(key);
				out.addNoDuplicate(fl);
				
			} catch (NamedProviderGetException e) {
				assert false;
			}
		}
		
	}
	
	public static <T extends FeatureInput> void addFeatureListToStoreNoDuplicateWithExtraName( FeatureList<T> featureList, String name, SharedFeatureSet<T> out ) {
		
		// We loop over all features in the ni, and call them all the same thing with a number
		for( Feature<T> f : featureList) {
			
			String chosenName = determineFeatureName(f,name, featureList.size()==1);
			
			// We duplicate so that when run in parallel each thread has its own local state for each feature
			//  and uses seperate cached calculation lists
			out.add(chosenName, f );
		}
	}
	
	
	public static <T extends FeatureInput> void addFeatureListToStore( FeatureList<T> featureList, String name, NamedFeatureStore<T> out ) {
		
		// We loop over all features in the ni, and call them all the same thing with a number
		for( Feature<T> f : featureList) {
			
			String chosenName = determineFeatureName(f,name, featureList.size()==1);
			
			// We duplicate so that when run in parallel each thread has its own local state for each feature
			//  and uses seperate cached calculation lists
			out.add(chosenName, f.duplicateBean() );
		}
	}


	/**
	 * Names in the store take the form   nameParent.nameFeature
	 * unless useOnlyParentName is TRUE, in which case they are called simply nameParent
	 * 
	 * @param feature
	 * @param nameParent
	 * @param useOnlyParentName
	 */
	private static <T extends FeatureInput> String determineFeatureName( Feature<T> feature, String nameParent, boolean useOnlyParentName ) {
		if (useOnlyParentName) {
			return nameParent;
		} else {
			return String.format("%s.%s", nameParent, feature.getFriendlyName() );
		}
	}
}
