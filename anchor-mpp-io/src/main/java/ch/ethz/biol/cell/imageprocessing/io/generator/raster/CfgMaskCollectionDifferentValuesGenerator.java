package ch.ethz.biol.cell.imageprocessing.io.generator.raster;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;

/*
 * #%L
 * anchor-mpp-io
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


import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.objmask.ObjMaskCollectionDifferentValuesGenerator;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithPropertiesCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.OutputWriteFailedException;

import ch.ethz.biol.cell.mpp.cfg.Cfg;

public class CfgMaskCollectionDifferentValuesGenerator extends RasterGenerator implements IterableGenerator<Cfg> {

	private ObjMaskCollectionDifferentValuesGenerator delegate;
	private Cfg cfg;
	private RegionMembershipWithFlags rm;
	
	public CfgMaskCollectionDifferentValuesGenerator( ImageDim dim, RegionMembershipWithFlags rm ) {
		delegate = new ObjMaskCollectionDifferentValuesGenerator(dim);
		this.rm = rm;
	}
	
	public CfgMaskCollectionDifferentValuesGenerator( ImageDim dim, RegionMembershipWithFlags rm, Cfg cfg ) {
		this( dim, rm );
		this.cfg = cfg;
	}
	
	@Override
	public boolean isRGB() {
		return delegate.isRGB();
	}

	@Override
	public Stack generate() throws OutputWriteFailedException {

		ObjMaskWithPropertiesCollection masks = cfg.calcMask(
			delegate.getDimensions(),
			this.rm,
			BinaryValuesByte.getDefault(),
			null
		);
		try {
			delegate.setIterableElement(masks.collectionObjMask());
		} catch (SetOperationFailedException e) {
			throw new OutputWriteFailedException(e);
		}
		return delegate.generate();
	}

	@Override
	public ManifestDescription createManifestDescription() {
		return delegate.createManifestDescription();
	}

	@Override
	public Cfg getIterableElement() {
		return cfg;
	}

	@Override
	public void setIterableElement(Cfg element)
			throws SetOperationFailedException {
		this.cfg = element;
	}

	@Override
	public void start() throws OutputWriteFailedException {
		delegate.start();
	}

	@Override
	public void end() throws OutputWriteFailedException {
		delegate.end();
	}

	@Override
	public Generator getGenerator() {
		return this;
	}

}
