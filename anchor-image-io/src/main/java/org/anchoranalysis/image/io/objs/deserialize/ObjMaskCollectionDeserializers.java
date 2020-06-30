package org.anchoranalysis.image.io.objs.deserialize;

import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.bean.deserializer.Deserializer;

/** Entry point to deserializers */
public class ObjMaskCollectionDeserializers {
	
	public static final Deserializer<ObjectCollection> tiffCorrectMissing = new ReadObjsFromTIFFDirectoryCorrectMissing();
	public static final Deserializer<ObjectCollection> hdf5 = new ReadObjsFromHDF5();
}
