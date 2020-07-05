package org.anchoranalysis.image.io.objs.deserialize;

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
import java.util.List;

import org.anchoranalysis.image.io.objs.GeneratorHDF5;
import org.anchoranalysis.image.io.objs.PathUtilities;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.error.AnchorIOException;

import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5Reader;
import ncsa.hdf.hdf5lib.exceptions.HDF5FileNotFoundException;

class ReadObjsFromHDF5 implements Deserializer<ObjectCollection> {

	private ObjMaskHDF5Reader objReader = new ObjMaskHDF5Reader();
	
	@Override
	public ObjectCollection deserialize(Path path) throws DeserializationFailedException {

		try (IHDF5Reader reader = HDF5Factory.openForReading(path.toString())) {

			return readObjs(reader, PathUtilities.OBJS_ROOT_PATH );
		
		} catch (HDF5FileNotFoundException e) {
			throw new DeserializationFailedException(
				String.format("HDF5 file not found at %s", path)	
			);
		} catch (Exception e) {
			throw new DeserializationFailedException(
				String.format(
					"An error occurred while reading HDF5 at %s with rootPath=%s",
					path,
					PathUtilities.OBJS_ROOT_PATH
				),
				e
			);
		}
	}
	
	/**
	 * Read all objects
	 * 
	 * @param reader
	 * @param rootPath a path in the HDF5, NOTE it should always end in a forward-slash
	 * @return
	 * @throws DeserializationFailedException 
	 * @throws AnchorIOException 
	 */
	private ObjectCollection readObjs(IHDF5Reader reader, String rootPath) throws DeserializationFailedException {

		assert( rootPath.endsWith("/") );
				
		// First check the number of objects expected
		// if the the rootPath exists in the HDF5, if not, it's an indication that there's no
		// objects present
		int numObjs = ObjMaskHDF5Reader.extractIntAttr(reader.uint32(), "/", GeneratorHDF5.NUM_OBJS_ATTR_NAME);
		if (numObjs==0) {
			return ObjectCollectionFactory.empty();
		}
		
		ObjectCollection out = readObjsNoCheck(reader, rootPath);
				
		if (out.size()!=numObjs) {
			throw new DeserializationFailedException(
				String.format(
					"An error occurred deserializing HDF5, where %d objects were expected but %d were read.",
					numObjs,
					out.size()
				)	
			);
		}
		return out;
	}

	/** Reads the objects without doing any check on the total number of objects expected or received */
	private ObjectCollection readObjsNoCheck(IHDF5Reader reader, String rootPath) {

		List<String> groups = reader.object().getAllGroupMembers( rootPath );
		return ObjectCollectionFactory.mapFrom(
			groups,
			groupName->	objReader.apply(reader, rootPath + groupName)
		);
	}
}
