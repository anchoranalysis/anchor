/* (C)2020 */
package org.anchoranalysis.io.generator.sequence;

import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public interface GeneratorSequenceIncremental<T> {

    void start() throws OutputWriteFailedException;

    void add(T element) throws OutputWriteFailedException;

    void end() throws OutputWriteFailedException;
}
