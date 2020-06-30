package org.anchoranalysis.image.io.objs;

import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.generator.IterableGenerator;

public class ObjectMaskCollectionWriter {
	
	private ObjectMaskCollectionWriter() {}
	
	public static IterableGenerator<ObjectCollection> generator() {
		return new GeneratorHDF5(true);
	}
}
