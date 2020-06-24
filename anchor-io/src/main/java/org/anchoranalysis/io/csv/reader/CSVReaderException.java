package org.anchoranalysis.io.csv.reader;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

public class CSVReaderException extends AnchorFriendlyCheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CSVReaderException(String message) {
		super(message);
	}	
	
	public CSVReaderException(Throwable cause) {
		super(cause);
	}
}
