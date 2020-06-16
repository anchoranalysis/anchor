package org.anchoranalysis.image.experiment.bean.sgmn;

import java.util.Optional;

/*-
 * #%L
 * anchor-image-experiment
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.bean.NullParamsBean;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.objectmask.ObjectMaskCollection;
import org.anchoranalysis.image.seed.SeedCollection;
import org.anchoranalysis.image.sgmn.SgmnFailedException;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.io.output.bound.BoundIOContext;

public abstract class SgmnObjMaskCollection extends NullParamsBean<SgmnObjMaskCollection> {

	/**
	 * Performs the segmentation
	 * 
	 * @param stackCollection existing-stacks
	 * @param objMaskCollection existing-objects
	 * @param seeds
	 * @param re
	 * @param context
	 * @return
	 * @throws SgmnFailedException
	 */
	public abstract ObjectMaskCollection sgmn( NamedImgStackCollection stackCollection, NamedProvider<ObjectMaskCollection> objMaskCollection, Optional<SeedCollection> seeds, RandomNumberGenerator re, BoundIOContext context ) throws SgmnFailedException;
}
