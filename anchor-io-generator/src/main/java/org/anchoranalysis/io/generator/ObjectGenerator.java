package org.anchoranalysis.io.generator;

import org.anchoranalysis.io.output.error.OutputWriteFailedException;

// Generates an object, that is subsequently written to the file system
public abstract class ObjectGenerator<S> extends SingleFileTypeGenerator {

	public abstract S generate() throws OutputWriteFailedException;
}
