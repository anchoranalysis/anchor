package org.anchoranalysis.feature.input.descriptor;

/*
 * #%L
 * anchor-feature
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


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

public class FeatureInputDescriptorUtilities {

	private FeatureInputDescriptorUtilities() {}
	
	public static FeatureInputDescriptor paramTypeForTwo( Feature<?> item1, Feature<?> item2 ) {
		return paramTypeForTwo( item1.inputDescriptor(), item2.inputDescriptor() );
	}
	
	public static FeatureInputDescriptor paramTypeForTwo( FeatureInputDescriptor c1, FeatureInputDescriptor c2 ) {
		
		if (c1.isCompatibleWithEverything()) {
			return c2;
		}
		
		if (c2.isCompatibleWithEverything()) {
			return c1;
		}
		
		if (c1.equals(c2)) {
			return c1;
		}
		
		Optional<FeatureInputDescriptor> preferred = c1.preferToBidirectional(c2);
		if (!preferred.isPresent()) {
			throw new AnchorFriendlyRuntimeException("item1 and item2 must accept the same paramType, or be compatible.");
		}
		
		return preferred.get();	
	}
	
	
	public static <T extends FeatureInput> FeatureInputDescriptor paramTypeForThree( Feature<T> item1, Feature<T> item2, Feature<T> item3 ) {
		return paramTypeForList(
			Arrays.asList(item1,item2,item3)
		);
	}
	
	
	public static <T extends FeatureInput> FeatureInputDescriptor paramTypeForList( List<Feature<T>> list ) {
		
		if (list.size()==0) {
			return FeatureInputGenericDescriptor.instance;
		}
		
		FeatureInputDescriptor chosenParamType = chooseParamType(list); 
		
		if (chosenParamType==null) {
			return FeatureInputGenericDescriptor.instance;
		} else {
			return chosenParamType;
		}
		
	}
	
	private static <T extends FeatureInput> FeatureInputDescriptor chooseParamType( List<Feature<T>> list ) {
		FeatureInputDescriptor chosenParamType = null;
		for (Feature<?> f : list) {
			
			FeatureInputDescriptor paramType = f.inputDescriptor();
			
			if (paramType.isCompatibleWithEverything()) {
				continue;
			}
			
			if (chosenParamType==null) {
				chosenParamType = paramType;
			} else {
				if (!chosenParamType.equals(paramType)) {
					
					Optional<FeatureInputDescriptor> preferred = paramType.preferToBidirectional(chosenParamType);
					if (!preferred.isPresent()) {
						// We don't know which parameter to prefer
						throw new AnchorFriendlyRuntimeException("All features in the list must have the same paramType, or a simple type, or a preference between conflicting type");
					}
					chosenParamType = preferred.get();
				}
			}
		}
		return chosenParamType;
	}
}
