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
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.file.PathUtilities;
import org.anchoranalysis.io.params.InputContextParams;


/**
 * Predicates that matches a file-path against a regular expression
 * 
 * @author owen
 *
 */
public class MatchRegEx extends FileMatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN FIELDS
	/**
	 * If true, the filter is applied to the path as a whole, not just the filename (using forward slashes as directory seperators)
	 */
	@BeanField
	private boolean applyToPath = false;
	
	@BeanField
	private String expression;
	// END BEAN FIELDS
	
	public MatchRegEx() {
	}

	@Override
	protected Predicate<Path> createMatcherFile(Path dir, InputContextParams inputContext) {
		if (applyToPath) {
			Pattern pattern = Pattern.compile(expression);
			return p -> acceptPathViaRegEx(p, pattern);
		} else {
			return PathMatcherUtilities.filter(dir, "regex", expression);
		}
	}
	
	public static boolean acceptPathViaRegEx(Path path, Pattern pattern) {
		return pattern.matcher(
			pathAsString(path)
		).matches();
	}
	
	private static String pathAsString(Path p) {
		return PathUtilities.toStringUnixStyle(
			p.toFile().toString()
		);
	}

	public boolean isApplyToPath() {
		return applyToPath;
	}

	public void setApplyToPath(boolean applyToPath) {
		this.applyToPath = applyToPath;
	}
	
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
}