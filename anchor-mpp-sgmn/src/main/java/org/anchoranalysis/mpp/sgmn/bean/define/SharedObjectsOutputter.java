package org.anchoranalysis.mpp.sgmn.bean.define;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.image.bean.nonbean.init.CreateCombinedStack;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.io.stack.StackCollectionOutputter;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.mpp.io.output.StackOutputKeys;

class SharedObjectsOutputter {
	
	public static void output(
		ImageInitParams imageInit,
		boolean suppressSubfolders,
		BoundIOContext context
	) {
		StackCollectionOutputter.outputSubset(
			CreateCombinedStack.apply(imageInit),
			StackOutputKeys.STACK,
			suppressSubfolders,
			context
		);
	}
	
	public static void outputWithException(
		ImageInitParams imageInit,
		boolean suppressSubfolders,
		BoundIOContext context
	) throws OutputWriteFailedException {
		StackCollectionOutputter.outputSubsetWithException(
			CreateCombinedStack.apply(imageInit),
			context.getOutputManager(),
			StackOutputKeys.STACK,
			suppressSubfolders
		);
	}

	
	public static void output(
			MPPInitParams soMPP,
			boolean suppressSubfolders,
			BoundIOContext context
		) {
			ErrorReporter errorReporter = context.getErrorReporter();
			BoundOutputManagerRouteErrors outputManager = context.getOutputManager();
					
			SubsetOutputterFactory factory = new SubsetOutputterFactory(soMPP, outputManager, suppressSubfolders);
			factory.cfg().outputSubset(errorReporter);
			factory.histogram().outputSubset(errorReporter);
			factory.objMask().outputSubset(errorReporter);
		}
	
	public static void outputWithException(
		MPPInitParams soMPP,
		BoundOutputManagerRouteErrors outputManager,
		boolean suppressSubfolders
	) throws OutputWriteFailedException {
		assert(outputManager.getOutputWriteSettings().hasBeenInit());	

		SubsetOutputterFactory factory = new SubsetOutputterFactory(soMPP, outputManager, suppressSubfolders);
		factory.cfg().outputSubsetWithException();
		factory.histogram().outputSubsetWithException();
		factory.objMask().outputSubsetWithException();
	}

	

}
