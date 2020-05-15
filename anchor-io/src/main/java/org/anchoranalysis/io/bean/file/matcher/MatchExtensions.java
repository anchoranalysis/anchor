package org.anchoranalysis.io.bean.file.matcher;

/*-
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.nio.file.Path;
import java.util.Set;
import java.util.function.Predicate;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.io.params.InputContextParams;
import org.apache.commons.io.FilenameUtils;


/**
 * Maybe imposes a file-extension condition, optionally on top of an existing matcher
 * 
 * <p>The extensions are always checked in a case-insensitive manner</p>
 * 
 * @author owen
 *
 */
public class MatchExtensions extends FileMatcher {

	// START BEAN PROPERTIES
	@BeanField @OptionalBean
	private FileMatcher matcher;
	
	/** 
	 * A set of file-extensions (without the period), one of which must match the end of a path.
	 * <p>If an empty set is passed then, no check occurs, and no extension is checked</p> 
	 * <p>If null, then a default set is populated from the inputContext</p>
	 */
	@BeanField @OptionalBean
	private Set<String> extensions;
	// END BEAN PROPERTIES

	@Override
	protected Predicate<Path> createMatcherFile(Path dir, InputContextParams inputContext) {
				
		Set<String> fileExtensions = fileExtensions(inputContext);
		
		if (matcher!=null) {
			Predicate<Path> firstPred = matcher.createMatcherFile(dir, inputContext);
			return p -> firstPred.test(p) && matchesAnyExtension(p, fileExtensions);
		} else {
			return p -> matchesAnyExtension(p, fileExtensions);
		}
	}
	
	// Does a path end with at least one of the extensions? Or fileExtensions are empty.
	private boolean matchesAnyExtension(Path path, Set<String> fileExtensions) {
		
		if (fileExtensions.isEmpty()) {
			// Note SPECIAL CASE. When empty, the check isn't applied, so is always TRUE
			return true;
		}
		
		// Extract extension from path
		String ext = FilenameUtils.getExtension( path.toString() ).toLowerCase();
		
		return fileExtensions.contains(ext);
	}
	
	private Set<String> fileExtensions(InputContextParams inputContext) {
		if (extensions!=null) {
			return extensions;
		} else {
			return inputContext.getInputFilterExtensions();
		}
	}

	public Set<String> getExtensions() {
		return extensions;
	}

	public void setExtensions(Set<String> extensions) {
		this.extensions = extensions;
	}

	public FileMatcher getMatcher() {
		return matcher;
	}

	public void setMatcher(FileMatcher matcher) {
		this.matcher = matcher;
	}
}
