package org.anchoranalysis.image.io.stack;

/*
 * #%L
 * anchor-image-io
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


import java.util.Set;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.name.provider.OperationFromNamedProvider;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.core.progress.ProgressReporterOneOfMany;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.generator.raster.StackGenerator;
import org.anchoranalysis.image.io.input.series.NamedChnlCollectionForSeries;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.bean.output.allowed.OutputAllowed;
import org.anchoranalysis.io.generator.collection.IterableGeneratorOutputHelper;
import org.anchoranalysis.io.output.OutputWriteFailedException;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

public class StackCollectionOutputter {
	
	/** Only outputs stacks whose names are allowed by the StackCollection part of the OutputManager 
	 * @throws OutputWriteFailedException */
	public static void outputSubset(
		INamedProvider<Stack> stacks,
		BoundOutputManagerRouteErrors outputManager,
		String secondLevelOutputKey,
		boolean suppressSubfolders,
		ErrorReporter errorReporter
	) {
		assert(outputManager.getOutputWriteSettings().hasBeenInit());		
		StackCollectionOutputter.output(
			stackSubset(stacks, secondLevelOutputKey, outputManager ),
			outputManager.getDelegate(),
			"stackCollection",
			"stack_",
			errorReporter,
			suppressSubfolders
		);
	}
	
	/** Only outputs stacks whose names are allowed by the StackCollection part of the OutputManager 
	 * @throws OutputWriteFailedException */
	public static void outputSubsetWithException(
		INamedProvider<Stack> stacks,
		BoundOutputManagerRouteErrors outputManager,
		String secondLevelOutputKey,
		boolean suppressSubfolders
	) throws OutputWriteFailedException {
		assert(outputManager.getOutputWriteSettings().hasBeenInit());		
		StackCollectionOutputter.outputWithException(
			stackSubset(stacks, secondLevelOutputKey, outputManager ),
			outputManager.getDelegate(),
			"stackCollection",
			"stack_",
			suppressSubfolders
		);
	}
	
	public static void output( NamedImgStackCollection namedCollection, BoundOutputManager outputManager, String outputName, String suffix, ErrorReporter errorReporter, boolean suppressSubfoldersIn) {
		StackGenerator generator = createStackGenerator();
		IterableGeneratorOutputHelper.output( namedCollection, generator, outputManager, outputName, suffix, errorReporter, suppressSubfoldersIn);
	}
	
	private static void outputWithException(NamedImgStackCollection namedCollection, BoundOutputManager outputManager, String outputName, String suffix, boolean suppressSubfoldersIn ) throws OutputWriteFailedException {
		StackGenerator generator = createStackGenerator();
		IterableGeneratorOutputHelper.outputWithException( namedCollection, generator, outputManager, outputName, suffix, suppressSubfoldersIn);
	}
	
	public static NamedImgStackCollection subset( INamedProvider<Stack> stackCollection, OutputAllowed oa ) {
		
		NamedImgStackCollection out = new NamedImgStackCollection();
		
		for ( String name : stackCollection.keys() ) {
			
			if (oa.isOutputAllowed(name)) {
				out.addImageStack(name, new OperationFromNamedProvider<Stack>(stackCollection, name) );
			}
		}
		
		return out;
	}
	
	public static void copyFrom( NamedChnlCollectionForSeries src, NamedImgStackCollection target, ProgressReporter progressReporter ) throws RasterIOException {
		
		Set<String> keys = src.chnlNames();
		
		try( ProgressReporterMultiple prm = new ProgressReporterMultiple(progressReporter, keys.size()) ) {
			for (String id : keys) {
				if (target.getImageNoException(id)==null) {
					target.addImageStack(id, new Stack( src.getChnl(id, 0, new ProgressReporterOneOfMany(prm)) ) );
					prm.incrWorker();
				}
			}
		}
	}
		
	private static StackGenerator createStackGenerator() {
		String manifestFunction = "stackFromCollection";
		return new StackGenerator(
			true,
			manifestFunction
		);
	}
	
	private static NamedImgStackCollection stackSubset( INamedProvider<Stack> stacks, String secondLevelOutputKey, BoundOutputManagerRouteErrors outputManager ) {
		return StackCollectionOutputter.subset(
			stacks,
			outputManager.outputAllowedSecondLevel(secondLevelOutputKey)
		);
	}
}
