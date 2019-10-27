package org.anchoranalysis.image.io.generator.raster;

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


import java.nio.ByteBuffer;

import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.chnl.factory.ChnlFactorySingleType;
import org.anchoranalysis.image.chnl.factory.ChnlFactoryByte;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.OutputWriteFailedException;

public class VoxelBoxGenerator extends RasterGenerator implements IterableObjectGenerator<VoxelBox<ByteBuffer>,Stack> {

	private VoxelBox<ByteBuffer> voxelBox;
	private String manifestFunction;
	
	private ChnlFactorySingleType factory = new ChnlFactoryByte();
	
	public VoxelBoxGenerator() {
		
	}
	
	public VoxelBoxGenerator(VoxelBox<ByteBuffer> voxelBox) {
		super();
		this.voxelBox = voxelBox;
		this.manifestFunction = "voxelBox";
	}

	@Override
	public Stack generate() throws OutputWriteFailedException {
		
		if (getIterableElement()==null) {
			throw new OutputWriteFailedException("no mutable element set");
		}
		
		Chnl chnl = factory.create(getIterableElement(), new ImageRes() );
		return new Stack( chnl );
	}

	@Override
	public VoxelBox<ByteBuffer> getIterableElement() {
		return voxelBox;
	}

	@Override
	public void setIterableElement(VoxelBox<ByteBuffer> element) {
		this.voxelBox = element;
	}

	@Override
	public ObjectGenerator<Stack> getGenerator() {
		return this;
	}

	@Override
	public ManifestDescription createManifestDescription() {
		return new ManifestDescription("raster", manifestFunction);
	}
	
	@Override
	public void start() throws OutputWriteFailedException {
	}


	@Override
	public void end() throws OutputWriteFailedException {
	}

	@Override
	public boolean isRGB() {
		return false;
	}

}
