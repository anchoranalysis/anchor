package org.anchoranalysis.image.io.objs.deserialize;

import org.anchoranalysis.image.objectmask.ObjectMaskCollection;
import org.anchoranalysis.io.bean.deserializer.Deserializer;

/** Entry point to deserializers */
public class ObjMaskCollectionDeserializers {
	
	public static final Deserializer<ObjectMaskCollection> tiffCorrectMissing = new ReadObjsFromTIFFDirectoryCorrectMissing();
	public static final Deserializer<ObjectMaskCollection> hdf5 = new ReadObjsFromHDF5();
}
