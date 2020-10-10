package org.anchoranalysis.io.generator.sequence;

import java.util.Optional;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.recorded.RecordingWriters;

/**
 * A sequence of elements that are written to the same directory.

 * @author Owen Feehan
 */
public interface OutputSequence extends AutoCloseable {

    boolean isOn();
    
    Optional<RecordingWriters> writers();
    
    /**
     * This should be called once after <i>all</i> elements have been written.
     * 
     * @throws OutputWriteFailedException if anything goes wrong.
     */
    void close() throws OutputWriteFailedException;
}
