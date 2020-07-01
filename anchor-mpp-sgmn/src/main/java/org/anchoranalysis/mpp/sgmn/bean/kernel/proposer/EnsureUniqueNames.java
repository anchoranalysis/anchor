package org.anchoranalysis.mpp.sgmn.bean.kernel.proposer;

/*-
 * #%L
 * anchor-mpp-sgmn
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.anchoranalysis.mpp.sgmn.kernel.proposer.WeightedKernel;

class EnsureUniqueNames {

	private EnsureUniqueNames() {}
	
	public static <T> void apply( List<WeightedKernel<T>> lstKernelFactories ) {
		Map<String,Integer> hashName = cntUniqueNames(lstKernelFactories);
		appendIntegerIfNecessary(lstKernelFactories, hashName);
	}

	private static <T> Map<String,Integer> cntUniqueNames( List<WeightedKernel<T>> lstKernelFactories) {
		
		HashMap<String,Integer> hashName = new HashMap<>(); 
		
		for( WeightedKernel<?> wkf : lstKernelFactories) {
			
			String name = wkf.getKernel().getBeanName();
			if (hashName.get(name)==null) {
				hashName.put(name, 1);
			} else {
				int val = hashName.get(name);
				hashName.put(name, ++val);
			}
		}
		
		return hashName;
	}
	
	// Now append an integer index to each kernel name that appears multiple times
	private static <T> void appendIntegerIfNecessary(
		List<WeightedKernel<T>> lstKernelFactories,
		Map<String,Integer> hashName
	) {
		HashMap<String,Integer> hashRunning = new HashMap<>();

		for( WeightedKernel<?> wkf : lstKernelFactories) {
			
			String name = wkf.getKernel().getBeanName();
			
			int valTotal = hashName.get(name);
			assert valTotal>0;
			
			if (valTotal==1) {
				// do nothing as there is only a single instance of the name
			} else {
			
				int indexToAppend;
				if (hashRunning.get(name)==null) {
					hashRunning.put(name, 0);
					indexToAppend = 0;
				} else {
					int valRunning = hashRunning.get(name);
					hashRunning.put(name, ++valRunning);
					indexToAppend = valRunning;
				}
				
				String newName = String.format("%s_%d", name, indexToAppend);
				wkf.setName(newName);
			}
		}
	}
		
	
}
