package org.anchoranalysis.io.output.file;

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


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.manifest.ManifestDescription;

public class FileOutput extends AnchorBean<FileOutput> {

	// START BEAN PROPERTIES
	
	// If set we output CSV to this path
	@BeanField
	private String filePath = null;
	
	@BeanField
	private String extension;
	
	@BeanField
	private ManifestDescription manifestDescription;
	
	// END BEAN PROPERTIES
	
	// Files for writing out
	private PrintWriter out;
	
	public FileOutput(String filePath, String extension, ManifestDescription manifestDescription ) {
		super();
		this.filePath = filePath;
		this.extension = extension;
		this.manifestDescription = manifestDescription;
	}
	
	public PrintWriter getWriter() {
		return this.out;
	}
	
	
	public void start() throws AnchorIOException {
		
		try {
			FileWriter fileWriter = new FileWriter( filePath );
			
			this.out = new PrintWriter(fileWriter);
		} catch (IOException e) {
			throw new AnchorIOException("Cannot create file-writer", e);
		}
	}
	
	public boolean isEnabled() {
		return (out!=null);
	}
		
	public void end() {
		if (out!=null) {
			out.close();
		}
	}
	
	
	public String getFilePath() {
		return filePath;
	}


	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public ManifestDescription getManifestDescription() {
		return manifestDescription;
	}

	public void setManifestDescription(ManifestDescription manifestDescription) {
		this.manifestDescription = manifestDescription;
	}


}
