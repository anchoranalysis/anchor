package org.anchoranalysis.io.input.csv;

import org.anchoranalysis.core.exception.OperationFailedException;

/**
 * Called when a single line is read by a CSV-reader to determine how the entry is further processed.
 * 
 * @author Owen Feehan
 *
 */
@FunctionalInterface
public interface ProcessCSVLine {
	
	/**
	 * Called when a single line is read by a CSV-reader to determine how the entry is further processed.
	 * 
	 * @param line the line.
	 * @param firstLine true when it is the first line in the file, false otherwise.
	 * @throws OperationFailedException if an error occurs processing the line.
	 */
    void processLine(String[] line, boolean firstLine) throws OperationFailedException;
}