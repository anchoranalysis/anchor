package org.anchoranalysis.mpp.sgmn.bean.define;

import java.util.Optional;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.image.init.ImageInitParams;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
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
		return MPPInitParamsFactory.createFromInput(
			input,
			context,
			Optional.ofNullable(define)
		);
	}
	
	protected MPPInitParams createInitParams(BoundIOContext context) throws CreateException {
		return MPPInitParamsFactory.create(
			context,
			Optional.ofNullable(define)
		);
	}
	

	protected MPPInitParams createInitParams(
		BoundIOContext context,
		Optional<INamedProvider<Stack>> stacks,
		Optional<INamedProvider<ObjMaskCollection>> objs,
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
