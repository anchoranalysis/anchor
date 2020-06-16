package org.anchoranalysis.image.io.objs;

import org.anchoranalysis.image.objectmask.ObjectMaskCollection;
import org.anchoranalysis.io.generator.IterableGenerator;

public class ObjectMaskCollectionWriter {
	
	private ObjectMaskCollectionWriter() {}
	
	public static IterableGenerator<ObjectMaskCollection> generator() {
		return new GeneratorHDF5(true);
	}
}
