package org.anchoranalysis.io.csv.reader;

import java.io.IOException;
import java.nio.file.Path;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.csv.reader.CSVReaderByLine.ProcessCSVLine;
import org.anchoranalysis.io.csv.reader.CSVReaderByLine.ReadByLine;

class ReadByLineImpl implements ReadByLine {
	
	private CSVReader csvReader;
	private Path filePath;
	
	private CSVReader.OpenedCSVFile openedFile = null;
	
	public ReadByLineImpl(Path filePath, CSVReader csvReader) {
		super();
		this.filePath = filePath;
		this.csvReader = csvReader;
	}

	@Override
	public String[] headers() throws CSVReaderException {
		openIfNecessary();
		return openedFile.getHeaders();
	}
	
	@Override
	public int read( ProcessCSVLine lineProcessor ) throws CSVReaderException {

		try {
			openIfNecessary();
			
			String[] line;
			boolean firstLine = true; 
			
			int cnt = 0;
			
			while ( (line= openedFile.readLine())!=null ) {
				lineProcessor.processLine(line, firstLine);
				firstLine = false;
				cnt++;
			}
			
			return cnt;
			
		} catch (IOException | OperationFailedException e) {
			throw new CSVReaderException(e);
		} finally {
			close();
		}
	}
	
	private void openIfNecessary() throws CSVReaderException {
		if (openedFile==null) {
			openedFile = csvReader.read( filePath );
		}
	}

	/** Closes any opened-files 
	 * @throws CSVReaderException */
	@Override
	public void close() throws CSVReaderException {
		if (openedFile!=null) {
			openedFile.close();
			openedFile = null;
		}
	}
	
}