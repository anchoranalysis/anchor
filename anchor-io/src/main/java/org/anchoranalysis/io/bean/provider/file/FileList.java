package org.anchoranalysis.io.bean.provider.file;

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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.io.params.InputContextParams;

public class FileList extends FileProvider {

	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private List<String> listPaths = new ArrayList<>();
	// END BEAN PROPERTIES
	
	@Override
	public Collection<File> matchingFiles(ProgressReporter progressReporter, InputContextParams inputContext) throws FileNotFoundException {

		progressReporter.setMin(0);
		progressReporter.setMax( listPaths.size() );
		
		List<File> listOut = new ArrayList<>();
		
		for( int i=0; i<listPaths.size(); i++) {
			String s = listPaths.get(i);
			
			File f = new File(s);
			listOut.add(f);
			
			progressReporter.update(i+1);
		}
		
		return listOut;
	}

	public List<String> getListPaths() {
		return listPaths;
	}

	public void setListPaths(List<String> listPaths) {
		this.listPaths = listPaths;
	}

}
