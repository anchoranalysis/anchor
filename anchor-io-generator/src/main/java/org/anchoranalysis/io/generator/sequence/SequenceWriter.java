package org.anchoranalysis.io.generator.sequence;

import java.util.Optional;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public abstract class SequenceWriter {

	public abstract void init( FileType[] fileTypes, SequenceType sequenceType, boolean suppressSubfolder  ) throws InitException;
	
	public abstract boolean isOn();
	
	public abstract void write(Operation<Generator,OutputWriteFailedException> generator, String index) throws OutputWriteFailedException;
	
	public abstract Optional<BoundOutputManager> getOutputManagerForFiles();
}
