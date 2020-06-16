package org.anchoranalysis.image.io.objs;

/*-
 * #%L
 * anchor-image-io
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

import java.util.List;

import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.io.generator.raster.obj.ObjWithBoundingBoxGenerator;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.objectmask.ObjectCollection;
import org.anchoranalysis.io.generator.IterableGeneratorBridge;
import org.anchoranalysis.io.generator.collection.SubfolderGenerator;

/**
 * Writes the object-mask-collection as a TIFF to a directory
 * 
 * Writes the corner information as a binary-serialized file in the directory
 * 
 * @author FEEHANO
 *
 */
public class GeneratorTIFFDirectory extends IterableGeneratorBridge<ObjectCollection,List<ObjectMask>> {

	public GeneratorTIFFDirectory() {
		super(
			new SubfolderGenerator<ObjectMask,List<ObjectMask>>(
				new ObjWithBoundingBoxGenerator( new ImageRes() ),		// We don't specify a sceneres as we don't know what images they belong to
				"obj"
			),
			a -> a.asList()
		);
	}
}
