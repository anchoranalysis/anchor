package org.anchoranalysis.image.io.objs;

import static org.anchoranalysis.image.io.objs.deserialize.ObjMaskCollectionDeserializers.*;

/*
 * #%L
 * anchor-io
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


import java.nio.file.Files;
import java.nio.file.Path;

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;


/**
 * Reads an ObjMaskCollection from the filesystem
 * 
 * @author FEEHANO
 *
 */
public class ObjMaskCollectionReader {

	private static final String HDF5_EXTENSION = ".h5";
	
	/**
	 * Like createFromFolder but if the path is missing (and some other criteria are fulfilled), we pretend it's an empty collection
	 * 
	 * The following order is used to look for an object-mask collection
	 *   1. If path ends in .h5 it is read as a HDF5 object-mask collection
	 *   2. Otherwise, .h5 is suffixed, and if this path exists, it is read as a HDF5 object-mask collection
	 *   3. Otherwise, the path is assumed to be a directory, and this is read as a TIFFDirectory
	 *   
	 *   In the case of 3, if the path does not exist, but it is the subpath of an "ObjMaskCollection" directory which does
	 *   then a special case occurs. An empty ObjMaskCollection() is returned.
	 *   
	 * @param path path used to search for an ObjMaskCollection using the rules above
	 * @param rasterReader a rasterReader that can be used for reading the object-masks
	 * @return the obj-mask collection
	 * @throws CreateException if something goes wrong
	 * @throws DeserializationFailedException 
	 */
	public static ObjMaskCollection createFromPath( Path path ) throws DeserializationFailedException {

		// 1. First check if has a file extension HDF5
		if (hasHdf5Extension(path)) {
			return hdf5.deserialize(path);
		}
		
		// 2. Suffix a .h5 and see if the file exists
		Path suffixed = addHdf5Extension(path);
		if ( Files.exists(suffixed)) {
			return hdf5.deserialize(suffixed);
		}
		
		// 3. Treat as a folder of TIFFs
		return tiffCorrectMissing.deserialize(path);
	}

	
	public static Operation<ObjMaskCollection> createFromPathCached( Operation<Path> path ) {
		return () -> {
			try {
				return createFromPath(
					path.doOperation()
				);
			} catch (DeserializationFailedException e) {
				throw new ExecuteException(e);
			}
		};
	}
	
	public static boolean hasHdf5Extension( Path path ) {
		return path.toString().toLowerCase().endsWith(HDF5_EXTENSION);
	}
	
	private static Path addHdf5Extension( Path path ) {
		return path.resolveSibling(
			path.getFileName() + HDF5_EXTENSION	
		);
	}

}
