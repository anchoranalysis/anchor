/* (C)2020 */
package org.anchoranalysis.io.generator.sequence;

import java.util.Optional;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public interface SequenceWriter {

    void init(FileType[] fileTypes, SequenceType sequenceType, boolean suppressSubfolder)
            throws InitException;

    boolean isOn();

    void write(Operation<Generator, OutputWriteFailedException> generator, String index)
            throws OutputWriteFailedException;

    Optional<BoundOutputManager> getOutputManagerForFiles();
}
