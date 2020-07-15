package org.anchoranalysis.image.io.objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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

/** Paths to identify objects in the HDF5 for object-masks */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class HDF5PathHelper {
	
	/** Attribute for x dimension of extent */
	public static final String EXTENT_X = "x";
	
	/** Attribute for y dimension of extent */
	public static final String EXTENT_Y = "y";
	
	/** Attribute for z dimension of extent */
	public static final String EXTENT_Z = "z";
	
	/** Hardcoded string that identifies in the HDF5 file for where the object-collection is stored */
	public static final String OBJECTS_ROOT = "ObjMaskCollection";

	/** Adds seperators before and after the {@code OBJECTS_ROOT_PATH} */
	public static final String OBJECTS_ROOT_WITH_SEPERATORS = "/" + OBJECTS_ROOT + "/";	// NOSONAR
	
	public static String pathForObject( int index ) {
		return String.format("%s/%08d", OBJECTS_ROOT_WITH_SEPERATORS, index);
	}
}
