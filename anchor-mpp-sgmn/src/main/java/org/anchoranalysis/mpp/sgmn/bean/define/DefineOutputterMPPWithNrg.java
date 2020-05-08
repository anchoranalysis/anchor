package org.anchoranalysis.mpp.sgmn.bean.define;

import java.util.Optional;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.init.ImageInitParams;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.mpp.io.input.InputForMPPBean;
import org.anchoranalysis.mpp.io.output.NRGStackWriter;

public class DefineOutputterMPPWithNrg extends DefineOutputterWithNrg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 * @author Owen Feehan
	 *
	 * @param <T> init-params-type
	 * @param <S> return-type
	 */
	@FunctionalInterface
	public interface OperationWithNRGStack<T,S> {
		S process(T initParams, NRGStackWithParams nrgStack) throws OperationFailedException;
	}
	
	public <S> S processInput(
		InputForMPPBean input,
		BoundIOContext context,
		OperationWithNRGStack<ImageInitParams,S> operation
	) throws OperationFailedException {

		try {
			MPPInitParams initParams = super.createInitParams(input,context);
			return processWithNRGStack(initParams.getImage(), initParams.getImage(), operation, context);
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
	}
		
	public <S> S processInput(
		BoundIOContext context,
		Optional<INamedProvider<Stack>> stacks,
		Optional<INamedProvider<ObjMaskCollection>> objs,
		Optional<KeyValueParams> keyValueParams,
		OperationWithNRGStack<MPPInitParams,S> operation
	) throws OperationFailedException {
		try {
			MPPInitParams initParams = super.createInitParams(context, stacks, objs, keyValueParams);
			return processWithNRGStack(initParams, initParams.getImage(), operation, context);
			
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
	}
	
	private <T,S> S processWithNRGStack(
		T initParams,
		ImageInitParams imageParams,
		OperationWithNRGStack<T,S> operation,
		BoundIOContext context
	) throws OperationFailedException {
		try {
			NRGStackWithParams nrgStack = super.createNRGStack(
				imageParams,
				context.getLogger()
			);
			
			S result = operation.process(initParams, nrgStack);
			
			outputSharedObjs(imageParams, nrgStack, context);
			
			return result;
			
		} catch (InitException | CreateException | OutputWriteFailedException e) {
			throw new OperationFailedException(e);
		}			
	}
	
	
	// General objects can be outputted
	private void outputSharedObjs(ImageInitParams initParams, NRGStackWithParams nrgStack, BoundIOContext context) throws OutputWriteFailedException {
		
		super.outputSharedObjs(initParams, context);
		
		NRGStackWriter.writeNRGStack(
			nrgStack,
			context
		);
	}
}
