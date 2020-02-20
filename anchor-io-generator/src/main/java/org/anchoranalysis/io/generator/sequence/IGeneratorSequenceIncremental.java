package org.anchoranalysis.io.generator.sequence;

import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public interface IGeneratorSequenceIncremental<T> {

	void start() throws OutputWriteFailedException;
	
	void add( T element ) throws OutputWriteFailedException;
	
	void end() throws OutputWriteFailedException;
}
