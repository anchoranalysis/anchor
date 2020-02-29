package org.anchoranalysis.experiment.bean.identifier;

/*-
 * #%L
 * anchor-experiment
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.anchoranalysis.bean.annotation.BeanField;

/**
 * Automatically populates a experiment-name and version number
 * @author owen
 *
 */
public class ExperimentIdentifierAuto extends ExperimentIdentifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN FIELDS
	/** If there's no task-name, then this constant is used as a fallback name */
	@BeanField
	private String fallbackName = "experiment";
	// END BEAN FIELDS
	
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm");
	
	@Override
	public String identifier(String taskName) {
		return IdentifierUtilities.identifierFromNameVersion(
			determineExperimentName(taskName),
			determineVersion()
		);
	}

	private String determineExperimentName(String taskName) {
		if (taskName!=null) {
			return taskName;
		} else {
			return fallbackName;
		}
	}
	
	private String determineVersion() {
		return dtf.format( LocalDateTime.now() );
	}
}
