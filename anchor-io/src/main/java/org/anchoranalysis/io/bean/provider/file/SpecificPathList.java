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
import java.util.Optional;
import java.util.stream.Collectors;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.core.functional.FunctionalUtilities;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.FileProviderException;
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

	// START BEAN PROPERTIES
	/** If specified, this forms the list of paths which is provided as input. If not, then the input-context is asked. If still not, then the fallback. */
	@BeanField @OptionalBean
	private List<String> listPaths;
	
	/** If no paths can be found either from listPaths or the input-context, then the fallback is called if exists, otherwise an error is thrown */
	@BeanField @OptionalBean
	private FileProvider fallback;
	// END BEAN PROPERTIES
	
	public SpecificPathList() {
		
	}
	
	public SpecificPathList( List<String> listPaths ) {
		this.listPaths = listPaths;
	}
	
	/** Factory method for creating the class with an empty list of paths */
	public static SpecificPathList createWithEmptyList() {
		SpecificPathList out = new SpecificPathList();
		out.listPaths = new ArrayList<>();
		return out;
	}
	
	@Override
	public Collection<File> create(InputManagerParams params) throws FileProviderException {

		Optional<List<String>> selectedPaths = selectListPaths(params.getInputContext());
		
		if (selectedPaths.isPresent()) {
			return matchingFilesForList(
				selectedPaths.get(),
				params.getProgressReporter()
			);
			
		} else if (fallback!=null) {
			return fallback.create(params);
		} else {
			throw new FileProviderException("No input-paths are specified, nor a fallback");
		}
	}
	
	private Optional<List<String>> selectListPaths(InputContextParams inputContext) {
		
		if (listPaths!=null) {
			return Optional.of(listPaths);
		} else if (inputContext.hasInputPaths()) {
			return Optional.of(
				stringFromPaths(inputContext.getInputPaths())
			);
		} else {
			return Optional.empty();
		}
	}
	
	private static List<String> stringFromPaths( List<Path> paths ) {
		return paths.stream().map( s->s.toString() ).collect(Collectors.toList());
	}
	
	private static Collection<File> matchingFilesForList(List<String> listPaths, ProgressReporter progressReporter ) {
		return FunctionalUtilities.mapListWithProgress(
			listPaths,
			progressReporter,
			File::new
		);
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
