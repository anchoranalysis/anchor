/* (C)2020 */
package org.anchoranalysis.io.generator.sequence;

import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public interface GeneratorSequenceNonIncremental<T> {

    // totalNumAdd indicates in advance, how many times add will be called
    // If this is unknown, it should be set to -1
    // Not all writers support additions when this is unknown
    void start(SequenceType sequenceType, int totalNumAdd) throws OutputWriteFailedException;

    void add(T element, String index) throws OutputWriteFailedException;

    void end() throws OutputWriteFailedException;

    void setSuppressSubfolder(boolean suppressSubfolder);
}
