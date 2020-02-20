package org.anchoranalysis.image.io.generator.raster.seed;

import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.objmask.ObjMaskCollectionGenerator;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.seed.SeedCollection;
import org.anchoranalysis.image.seed.SeedsFactory;
import org.anchoranalysis.image.stack.Stack;

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


import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class SeedsAsCenterPointsGenerator extends RasterGenerator {

	private SeedCollection seeds;
	private ImageDim dim;
	
	public SeedsAsCenterPointsGenerator(SeedCollection seeds, ImageDim dim ) {
		super();
		this.seeds = seeds;
		this.dim = dim;
	}

	@Override
	public Stack generate() throws OutputWriteFailedException {
		ObjMaskCollection masks = createCentrePointMasks(
			seeds.createMasks(),
			BinaryValuesByte.getDefault()
		);
		return new ObjMaskCollectionGenerator(masks, dim).generate();
	}

	@Override
	public ManifestDescription createManifestDescription() {
		return new ManifestDescription("raster", "seedsAsCenterPoints");
	}

	@Override
	public boolean isRGB() {
		return false;
	}
		
	private ObjMaskCollection createCentrePointMasks( ObjMaskCollection in, BinaryValuesByte bv ) {
		ObjMaskCollection out = new ObjMaskCollection();
		for (ObjMask om : in) {
			out.add(
				SeedsFactory.create(om.getBoundingBox().midpoint(), bv).createMask()
			);
		}
		return out;
	}
}
