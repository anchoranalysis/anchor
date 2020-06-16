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
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.image.init.ImageInitParams;
import org.anchoranalysis.image.objectmask.ObjectMaskCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.mpp.io.input.InputForMPPBean;
import org.anchoranalysis.mpp.io.input.MPPInitParamsFactory;

public abstract class DefineOutputter extends AnchorBean<DefineOutputter> {

	// START BEAN PROPERTIES
	@BeanField @OptionalBean
	private Define define;
	
	@BeanField
	private boolean suppressSubfolders = false;
	
	@BeanField
	private boolean suppressOutputExceptions = false;
	// END BEAN PROPERTIES
	
	protected MPPInitParams createInitParams(
		InputForMPPBean input,
		BoundIOContext context
	) throws CreateException {
		return MPPInitParamsFactory.create(
			context,
			Optional.ofNullable(define),
			Optional.of(input)
		);
	}
	
	protected MPPInitParams createInitParams(BoundIOContext context) throws CreateException {
		return MPPInitParamsFactory.create(
			context,
			Optional.ofNullable(define),
			Optional.empty()
		);
	}
	

	protected MPPInitParams createInitParams(
		BoundIOContext context,
		Optional<NamedProvider<Stack>> stacks,
		Optional<NamedProvider<ObjectMaskCollection>> objs,
		Optional<KeyValueParams> keyValueParams
	) throws CreateException {
		return MPPInitParamsFactory.createFromExistingCollections(
			context,
			Optional.ofNullable(define),
			stacks,
			objs,
			keyValueParams
		);
	}
	
	// General objects can be outputted
	protected void outputSharedObjs(ImageInitParams initParams, BoundIOContext context) throws OutputWriteFailedException {
		if (suppressOutputExceptions) {
			SharedObjectsOutputter.output(initParams, suppressSubfolders, context);
		} else {
			SharedObjectsOutputter.outputWithException(initParams, suppressSubfolders, context);
		}
	}
	
	protected void outputSharedObjs(MPPInitParams initParams, BoundIOContext context) throws OutputWriteFailedException {
		
		outputSharedObjs(initParams.getImage(), context);
		
		if (suppressOutputExceptions) {
			SharedObjectsOutputter.output(initParams, suppressSubfolders, context);
		} else {
			SharedObjectsOutputter.outputWithException(initParams, context.getOutputManager(), suppressSubfolders);
		}
	}
	
	public Define getDefine() {
		return define;
	}

	public void setDefine(Define define) {
		this.define = define;
	}

	public boolean isSuppressSubfolders() {
		return suppressSubfolders;
	}

	public void setSuppressSubfolders(boolean suppressSubfolders) {
		this.suppressSubfolders = suppressSubfolders;
	}

	public boolean isSuppressOutputExceptions() {
		return suppressOutputExceptions;
	}

	public void setSuppressOutputExceptions(boolean suppressOutputExceptions) {
		this.suppressOutputExceptions = suppressOutputExceptions;
	}
}
