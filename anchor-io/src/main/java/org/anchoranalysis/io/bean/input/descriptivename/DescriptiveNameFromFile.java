package org.anchoranalysis.io.bean.input.descriptivename;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.CreateException;

public abstract class DescriptiveNameFromFile extends AnchorBean<DescriptiveNameFromFile> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected abstract String createDescriptiveName( File file, int index ) throws CreateException;
	
	private Logger log = Logger.getLogger(DescriptiveNameFromFile.class.getName());
	
	public String createDescriptiveNameOrElse( File file, int index, String elseName ) {
		try {
			return createDescriptiveName(file, index);
		} catch (CreateException e) {
			String msg = String.format(
				"Cannot create a descriptive-name for file %s and index %d. Using '%s' instead.",
				file.getPath(),
				index,
				elseName
			);
			log.log( Level.WARNING, msg, e );
			return elseName;
		}
	}
	
}
