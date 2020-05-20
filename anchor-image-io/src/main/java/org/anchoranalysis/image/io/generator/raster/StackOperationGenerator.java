package org.anchoranalysis.image.io.generator.raster;

import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class StackOperationGenerator extends RasterGenerator implements IterableObjectGenerator<
	Operation<Stack,OutputWriteFailedException>,
	Stack
> {

	private Operation<Stack,OutputWriteFailedException> stackIn;
	private boolean padIfNec;
	private String manifestFunction;
	
	public StackOperationGenerator(boolean padIfNec, String manifestFunction ) {
		super();
		this.padIfNec = padIfNec;
		this.manifestFunction = manifestFunction;
	}
	
	// Notes pads the passed channel, would be better if it makes a new stack first
	public StackOperationGenerator(
		Operation<Stack,OutputWriteFailedException> stack,
		boolean padIfNec,
		String manifestFunction
	) {
		super();
		this.stackIn = stack;
		this.padIfNec = padIfNec;
		this.manifestFunction = manifestFunction;
	}

	@Override
	public Stack generate() throws OutputWriteFailedException {
		assert( stackIn!=null);
		return StackGenerator.generateImgStack( stackIn.doOperation(), padIfNec );
	}

	@Override
	public ManifestDescription createManifestDescription() {
		return new ManifestDescription("raster", manifestFunction);
	}


	@Override
	public ObjectGenerator<Stack> getGenerator() {
		return this;
	}

	@Override
	public Operation<Stack,OutputWriteFailedException> getIterableElement() {
		return stackIn;
	}

	@Override
	public void setIterableElement(Operation<Stack,OutputWriteFailedException> element) {
		this.stackIn = element;
	}

	@Override
	public boolean isRGB() throws OutputWriteFailedException {
		return stackIn.doOperation().getNumChnl()==3 || (stackIn.doOperation().getNumChnl()==2 && padIfNec);
	}
}
