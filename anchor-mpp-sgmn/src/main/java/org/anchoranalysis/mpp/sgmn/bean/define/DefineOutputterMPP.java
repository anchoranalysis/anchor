package org.anchoranalysis.mpp.sgmn.bean.define;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.init.ImageInitParams;
import org.anchoranalysis.image.io.input.series.NamedChnlCollectionForSeries;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequenceStore;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.mpp.io.input.MultiInput;

/** 
 * Helper for tasks that uses a {@link Define} in association with an input to execute some tasks, and then outputs results
 *  * 
 * @param <T> input-object type
 * */
public class DefineOutputterMPP extends DefineOutputter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@FunctionalInterface
	public interface OperationWithImageInitParams {
		void process(ImageInitParams initParams) throws OperationFailedException;
	}
	
	public void processInput(
		NamedChnlCollectionForSeries ncc,
		BoundIOContext context
	) throws OperationFailedException {
		try {
			MPPInitParams initParams = super.createInitParams(context);
			ncc.addAsSeparateChnls(
				new WrapStackAsTimeSequenceStore(
					initParams.getImage().getStackCollection()
				),
				0
			);
			
			super.outputSharedObjs(initParams, context);
			
		} catch (CreateException | OutputWriteFailedException e) {
			throw new OperationFailedException(e);
		}
	}
	
	public void processInput(
		MultiInput input,
		BoundIOContext context,
		OperationWithImageInitParams operation
	) throws OperationFailedException {
		try {
			MPPInitParams initParams = super.createInitParams(input, context);
			
			operation.process(initParams.getImage());
			
			super.outputSharedObjs(initParams, context);
			
		} catch (CreateException | OutputWriteFailedException e) {
			throw new OperationFailedException(e);
		}
	}
}
