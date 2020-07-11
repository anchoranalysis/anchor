package org.anchoranalysis.core.log;

import java.nio.file.Path;

import lombok.RequiredArgsConstructor;
import lombok.Value;

/** A logger with additional configuration that are coupled together widely in the code */
@Value @RequiredArgsConstructor
public class CommonContext {
	
	/** The logger */
	private final Logger logger;
	
	/** Directory where machine-learning models can be found */
	private final Path modelDirectory;
}
