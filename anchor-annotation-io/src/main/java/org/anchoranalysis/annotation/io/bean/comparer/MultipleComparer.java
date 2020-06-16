package org.anchoranalysis.annotation.io.bean.comparer;

/*
 * #%L
 * anchor-annotation
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


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.anchoranalysis.annotation.AnnotationWithCfg;
import org.anchoranalysis.annotation.io.assignment.AssignmentObjMaskFactory;
import org.anchoranalysis.annotation.io.assignment.AssignmentOverlapFromPairs;
import org.anchoranalysis.annotation.io.assignment.generator.AssignmentGenerator;
import org.anchoranalysis.annotation.io.assignment.generator.AssignmentGeneratorFactory;
import org.anchoranalysis.annotation.io.assignment.generator.ColorPool;
import org.anchoranalysis.annotation.io.wholeimage.findable.Findable;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.NonEmpty;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.core.name.value.SimpleNameValue;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.image.feature.bean.evaluator.FeatureEvaluator;
import org.anchoranalysis.image.feature.objmask.pair.FeatureInputPairObjs;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.objectmask.ObjectCollection;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.bean.color.generator.ColorSetGenerator;
import org.anchoranalysis.io.bean.color.generator.VeryBrightColorSetGenerator;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

// Allows comparison of an annotation with multiple other entities
public class MultipleComparer extends AnchorBean<MultipleComparer> {

	// START BEAN PROPERTIES
	@BeanField
	private FeatureEvaluator<FeatureInputPairObjs> featureEvaluator;
	
	@BeanField @NonEmpty
	private List<NamedBean<Comparer>> listComparers = new ArrayList<NamedBean<Comparer>>();
	
	@BeanField
	private boolean useMIP = false;
	
	@BeanField
	private double maxCost = 1.0;
	// END BEAN PROPERTIES
	
	public List<NameValue<Stack>> createRasters(
		AnnotationWithCfg annotation,
		DisplayStack background,
		Path annotationPath,
		ColorSetGenerator colorSetGenerator,
		LogErrorReporter logErrorReporter,
		boolean debugMode
	) throws CreateException {
		
		SharedFeaturesInitParams so = SharedFeaturesInitParams.create(logErrorReporter);
		try {
			featureEvaluator.initRecursive( so, logErrorReporter );
		} catch (InitException e) {
			throw new CreateException(e);
		}
		
		List<NameValue<Stack>> out = new ArrayList<>();
		
		for( NamedBean<Comparer> ni : listComparers ) {
			
			ObjectCollection annotationObjs = annotation.convertToObjs(
				background.getDimensions()
			);
			
			Findable<ObjectCollection> compareObjs = ni.getValue().createObjs(annotationPath, background.getDimensions(), debugMode);
			
			Optional<ObjectCollection> foundObjs = compareObjs.getFoundOrLog(ni.getName(), logErrorReporter);
			
			if (!foundObjs.isPresent()) {
				continue;
			}
			
			out.add(
				compare(
					annotationObjs,
					foundObjs.get(),
					background,
					ni.getName(),
					colorSetGenerator
				)
			);
		}
		
		return out;
	}
	
	private SimpleNameValue<Stack> compare(
		ObjectCollection annotationObjs,
		ObjectCollection compareObjs,
		DisplayStack background,
		String rightName,
		ColorSetGenerator colorSetGenerator
	) throws CreateException {
		// Don't know how it's possible for an object with 0 pixels to end up here, but it's somehow happening, so we prevent it from interfereing
		//  with the rest of the analysis as a workaround
		removeObjsWithNoPixels( annotationObjs );
		removeObjsWithNoPixels( compareObjs );
				
		
		AssignmentOverlapFromPairs assignment;
		try {
			assignment = new AssignmentObjMaskFactory(featureEvaluator,useMIP).createAssignment(
				annotationObjs,
				compareObjs,
				maxCost,
				background.getDimensions()
			);
			
			ColorPool colorPool = new ColorPool(
				assignment.numPaired(),
				colorSetGenerator,
				new VeryBrightColorSetGenerator(),
				true
			);
			
			AssignmentGenerator generator = AssignmentGeneratorFactory.createAssignmentGenerator(
				background,
				assignment,
				colorPool,
				useMIP,
				"annotator",
				rightName,
				3,
				true
			);
			
			return new SimpleNameValue<>( rightName, generator.generate() );
			
		} catch (FeatureCalcException | OutputWriteFailedException e1) {
			throw new CreateException(e1);
		}		
	}
	
	private static void removeObjsWithNoPixels( ObjectCollection objs ) {
		
		Iterator<ObjectMask> itr = objs.iterator();
		while( itr.hasNext() ) {
			
			ObjectMask om = itr.next();
			
			if (om.numPixels()==0) {
				itr.remove();
			}
		}
	}
	
	public FeatureEvaluator<FeatureInputPairObjs> getFeatureEvaluator() {
		return featureEvaluator;
	}

	public void setFeatureEvaluator(FeatureEvaluator<FeatureInputPairObjs> featureEvaluator) {
		this.featureEvaluator = featureEvaluator;
	}

	public List<NamedBean<Comparer>> getListComparers() {
		return listComparers;
	}

	public void setListComparers(List<NamedBean<Comparer>> listComparers) {
		this.listComparers = listComparers;
	}


	public boolean isUseMIP() {
		return useMIP;
	}


	public void setUseMIP(boolean useMIP) {
		this.useMIP = useMIP;
	}


	public double getMaxCost() {
		return maxCost;
	}


	public void setMaxCost(double maxCost) {
		this.maxCost = maxCost;
	}


}
