package org.anchoranalysis.experiment.log.reporter;

/*
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


import java.io.PrintWriter;
import java.util.Optional;
import java.util.function.Supplier;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.file.FileOutput;

/**
 * Logs messages to a text-file.
 * <p>
 * Both the path of text-file can be determined in different ways by the constructor, and whether
 * it is written at all.
 * 
 * @author Owen Feehan
 *
 */
public class TextFileMessageLogger implements StatefulMessageLogger {

	// START REQUIRED ARGUMENTS
	private final Supplier<Optional<FileOutput>> fileOutputSupplier;
	private final ErrorReporter errorReporter;
	// END REQUIRED ARGUMENTS
	
	private Optional<FileOutput> fileOutput = Optional.empty();
	private Optional<PrintWriter> printWriter = Optional.empty();
	
	/**
	 * Constructs a logger that (always) writes messages to a text-file with a specific path.
	 * 
	 * @param filePath path to write the log to
	 * @param errorReporter error-reporter an error is outputted here if the log cannot be created, and no further logging occurs.
	 */
	public TextFileMessageLogger(String filePath, ErrorReporter errorReporter) {
		this.fileOutputSupplier = () -> Optional.of(
			new FileOutput(filePath)
		);
		this.errorReporter = errorReporter;
	}
		
	/**
	 * Constructs a logger that (maybe) writes messages to a text-file, with a path based upon an <i>output name</i> applied
	 * 	to a {@link BoundOutputManager}.
	 * <p>
	 * The message-log will only be outputted, if allowed by the {@link BoundOutputManager}.
	 * 
	 * @param outputName output-name
	 * @param outputManager output-manager
	 * @param errorReporter error-reporter an error is outputted here if the log cannot be created, and no further logging occurs.
	 */
	public TextFileMessageLogger(
		String outputName,
		BoundOutputManager outputManager,
		ErrorReporter errorReporter
	) {
		this.fileOutputSupplier = () -> TextFileLogHelper.createOutput(outputManager, outputName);
		this.errorReporter = errorReporter;
	}	

	@Override
	public void logFormatted(String formatString, Object... args) {
		log( String.format(formatString,args) );
	}
	
	@Override
	public void start() {
		try {
			fileOutput = fileOutputSupplier.get();
			printWriter = OptionalUtilities.map(
				fileOutput,
				output -> {
					output.start();
					return output.getWriter();
				}
			);
		} catch (Exception e) {
			errorReporter.recordError(MessageLogger.class, e);
		}		
	}

	@Override
	public void log( String message ) {
		printWriter.ifPresent( writer-> {
			synchronized(writer) {
				writer.print(message);
				writer.println();
				writer.flush();
			}
		});
	}
	
	@Override
	public void close(boolean successful) {
		fileOutput.ifPresent( FileOutput::end );
	}
}