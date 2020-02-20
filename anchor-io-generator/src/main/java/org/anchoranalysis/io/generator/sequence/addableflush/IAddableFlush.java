package org.anchoranalysis.io.generator.sequence.addableflush;

import org.anchoranalysis.io.output.error.OutputWriteFailedException;

// A time series of RGB images requiring a color index that is constant across frames
public interface IAddableFlush<T> {

	void add( T element ) throws OutputWriteFailedException;
	
	void flush() throws OutputWriteFailedException;
}
