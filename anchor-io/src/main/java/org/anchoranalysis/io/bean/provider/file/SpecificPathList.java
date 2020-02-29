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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.Optional;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.params.InputContextParams;

/**
 * A specific list of paths which form the input.
 * 
 * <p>If no paths are specified in the bean, then can be read from the Input-Context</p>
 * <p>If none are available in the Input-Context, then either the fallback is called if it exists, or an error is thrown</p>
 * @author owen
 *
 */
public class SpecificPathList extends FileProvider {

	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	/** If specified, this forms the list of paths which is provided as input. If not, then the input-context is asked. If still not, then the fallback. */
	@BeanField @Optional
	private List<String> listPaths;
	
	/** If no paths can be found either from listPaths or the input-context, then the fallback is called if exists, otherwise an error is thrown */
	@BeanField @Optional
	private FileProvider fallback;
	// END BEAN PROPERTIES
	
	@Override
	public Collection<File> matchingFiles(InputManagerParams params) throws AnchorIOException {

		List<String> selectedPaths = selectListPaths(params.getInputContext());
		
		if (selectedPaths!=null) {
			return matchingFilesForList(
				selectListPaths(params.getInputContext()),
				params.getProgressReporter()
			);
			
		} else if (fallback!=null) {
			return fallback.matchingFiles(params);
		} else {
			throw new AnchorIOException("No input-paths are specified, nor a fallback");
		}
	}
	
	private List<String> selectListPaths(InputContextParams inputContext) {
		
		if (listPaths!=null) {
			return listPaths;
		} else if (inputContext.hasInputPaths()) {
			return stringFromPaths(inputContext.getInputPaths());
		} else {
			return null;
		}
	}
	
	private static List<String> stringFromPaths( List<Path> paths ) {
		return paths.stream().map( s->s.toString() ).collect(Collectors.toList());
	}
	
	private static Collection<File> matchingFilesForList( List<String> listPaths, ProgressReporter progressReporter ) {
		
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

	public FileProvider getFallback() {
		return fallback;
	}

	public void setFallback(FileProvider fallback) {
		this.fallback = fallback;
	}

}
