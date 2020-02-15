package org.anchoranalysis.io.bean.input;

/*
 * #%L
 * anchor-io
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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.io.bean.input.descriptivename.DescriptiveNameFromFile;
import org.anchoranalysis.io.bean.input.descriptivename.LastFolders;
import org.anchoranalysis.io.bean.provider.file.FileProvider;
import org.anchoranalysis.io.input.FileInput;
import org.anchoranalysis.io.params.InputContextParams;


/**
 * File-paths 
 * 
 * @author Owen Feehan
 *
 */
public class Files extends InputManager<FileInput> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3838832669433747423L;
	
	// START BEAN PROPERTIES
	@BeanField
	private FileProvider fileProvider = null;
	
	@BeanField
	private DescriptiveNameFromFile descriptiveNameFromFile = new LastFolders(2);
	// END BEAN PROPERTIES
	
	public List<FileInput> inputObjects(InputContextParams inputContext, ProgressReporter progressReporter) throws IOException {
		
		List<FileInput> listOut = new ArrayList<>();
		
		Collection<File> files = getFileProvider().matchingFiles(progressReporter, inputContext );
		
		int index = 0;
		for( File f : files ) {
			
			String descriptiveName = descriptiveNameFromFile.createDescriptiveNameOrElse(
				f,
				index++,
				"<unknown>"
			);
			
			FileInput input = new FileInput(f, descriptiveName);
			listOut.add( input );
		}
		
		return listOut;
	}
	
	public FileProvider getFileProvider() {
		return fileProvider;
	}

	public void setFileProvider(FileProvider fileSet) {
		this.fileProvider = fileSet;
	}

	public DescriptiveNameFromFile getDescriptiveNameFromFile() {
		return descriptiveNameFromFile;
	}

	public void setDescriptiveNameFromFile(
			DescriptiveNameFromFile descriptiveNameFromFile) {
		this.descriptiveNameFromFile = descriptiveNameFromFile;
	}


}
