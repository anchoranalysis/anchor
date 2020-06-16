package org.anchoranalysis.image.io.generator.raster;

import java.util.Optional;

import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class ChnlGenerator extends RasterGenerator implements IterableObjectGenerator<Channel,Stack> {

	private Channel chnl = null;
	private String manifestFunction;
	
	public ChnlGenerator(String manifestFunction) {
		this.manifestFunction = manifestFunction;
	}
	
	public ChnlGenerator(Channel chnl) {
		this(chnl,"chnl");
	}
	
	public ChnlGenerator(Channel chnl, String manifestFunction) {
		super();
		this.chnl = chnl;
		this.manifestFunction = manifestFunction;
	}

	@Override
	public Stack generate() throws OutputWriteFailedException {
		
		if (getIterableElement()==null) {
			throw new OutputWriteFailedException("no mutable element set");
		}
		
		Stack stack = new Stack( getIterableElement() );
		return stack;
	}

	@Override
	public Channel getIterableElement() {
		return chnl;
	}

	@Override
	public void setIterableElement(Channel element) {
		this.chnl = element;
	}

	@Override
	public ObjectGenerator<Stack> getGenerator() {
		return this;
	}

	@Override
	public Optional<ManifestDescription> createManifestDescription() {
		return Optional.of(
			new ManifestDescription("raster", manifestFunction)
		);
	}

	@Override
	public boolean isRGB() {
		return false;
	}

}
