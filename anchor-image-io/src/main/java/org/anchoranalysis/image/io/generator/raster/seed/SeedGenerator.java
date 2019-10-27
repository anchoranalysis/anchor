package org.anchoranalysis.image.io.generator.raster.seed;

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


import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.objmask.rgb.RGBObjMaskGenerator;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithPropertiesCollection;
import org.anchoranalysis.image.seed.SeedCollection;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.bean.objmask.writer.RGBOutlineWriter;
import org.anchoranalysis.io.bean.output.OutputWriteSettings;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.OutputWriteFailedException;

import ch.ethz.biol.cell.imageprocessing.io.objmask.ObjMaskWriter;

public class SeedGenerator extends RasterGenerator {

	private RasterGenerator delegate;
	
	public SeedGenerator(ObjMaskWriter maskWriter, SeedCollection seeds, DisplayStack background, OutputWriteSettings outputSettings ) throws CreateException {
		super();
		
		ObjMaskCollection masks = seeds.createMasks();
		try {
			delegate = new RGBObjMaskGenerator(
				new RGBOutlineWriter(),
				new ObjMaskWithPropertiesCollection(masks),
				background,
				outputSettings.genDefaultColorIndex(seeds.size())
			);
		} catch (OperationFailedException e) {
			throw new CreateException(e);
		}
	}
	
	@Override
	public boolean isRGB() {
		return delegate.isRGB();
	}
	
	@Override
	public Stack generate() throws OutputWriteFailedException {
		return delegate.generate();
	}

	@Override
	public ManifestDescription createManifestDescription() {
		return new ManifestDescription("raster", "seed");
	}
	
	
}
