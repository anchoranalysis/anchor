package org.anchoranalysis.image.io.objects.deserialize;

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

import java.nio.file.Path;

import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;

class ReadObjsFromTIFFDirectoryCorrectMissing implements Deserializer<ObjectCollection> {

	private static final String OBJECT_DIRECTORY_NAME = "objMaskCollection";
	
	@Override
	public ObjectCollection deserialize(Path path) throws DeserializationFailedException {
		// Work around to tell the difference between a deliberately abandoned object-collection and an empty set
		if (isMissingButLooksLikeCollection(path) ) {
			return ObjectCollectionFactory.empty();
		} else {
			return new ReadObjsFromTIFFDirectory().deserialize(path);
		}
	}
	
	/** If the path is missing, but OBJECT_DIRECTORY_NAME is found as a parent-component */
	private static boolean isMissingButLooksLikeCollection( Path folderPath ) {
		if (!folderPath.toFile().exists()) {
			
			Path parent = folderPath.getParent();
			
			// Check if one folder up exists and is equal to OBJECT_DIRECTORY_NAME
			if (parent.toFile().exists() && namedAsObjectDirectory(parent)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean namedAsObjectDirectory(Path path) {
		return path.getName(
			path.getNameCount()-1
		).toString().equals(OBJECT_DIRECTORY_NAME);
	}
}
