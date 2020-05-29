package org.anchoranalysis.mpp.sgmn.bean.define;

/*-
 * #%L
 * anchor-mpp-sgmn
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import java.util.Optional;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
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
			return processWithNRGStack(initParams.getImage(), initParams.getImage(), initParams, operation, context);
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
	}
		
	public <S> S processInput(
		BoundIOContext context,
		Optional<NamedProvider<Stack>> stacks,
		Optional<NamedProvider<ObjMaskCollection>> objs,
		Optional<KeyValueParams> keyValueParams,
		OperationWithNRGStack<MPPInitParams,S> operation
	) throws OperationFailedException {
		try {
			MPPInitParams initParams = super.createInitParams(context, stacks, objs, keyValueParams);
			return processWithNRGStack(initParams, initParams.getImage(), initParams, operation, context);
			
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
	}
	
	private <T,S> S processWithNRGStack(
		T initParams,
		ImageInitParams imageParams,
		MPPInitParams mppParams,
		OperationWithNRGStack<T,S> operation,
		BoundIOContext context
	) throws OperationFailedException {
		try {
			NRGStackWithParams nrgStack = super.createNRGStack(
				imageParams,
				context.getLogger()
			);
			
			S result = operation.process(initParams, nrgStack);
			
			outputSharedObjs(mppParams, nrgStack, context);
			
			return result;
			
		} catch (InitException | CreateException | OutputWriteFailedException e) {
			throw new OperationFailedException(e);
		}			
	}
	
	
	// General objects can be outputted
	private void outputSharedObjs(MPPInitParams initParams, NRGStackWithParams nrgStack, BoundIOContext context) throws OutputWriteFailedException {
		
		super.outputSharedObjs(initParams, context);
		
		NRGStackWriter.writeNRGStack(
			nrgStack,
			context
		);
	}
}
