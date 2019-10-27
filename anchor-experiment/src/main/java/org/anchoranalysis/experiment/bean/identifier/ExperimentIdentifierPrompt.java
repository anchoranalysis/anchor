package org.anchoranalysis.experiment.bean.identifier;

/*
 * #%L
 * anchor-experiment
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


import javax.swing.JOptionPane;

import org.anchoranalysis.bean.annotation.BeanField;

public class ExperimentIdentifierPrompt extends ExperimentIdentifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private ExperimentIdentifier experimentIdentifier;
	// END BEAN PROPERTIES

	private String identifier;
	
	@Override
	public void init(boolean gui) {
		experimentIdentifier.init(gui);

		identifier = experimentIdentifier.identifier();
		
		if (!gui) {
			return;
		}
		
		String s = (String) JOptionPane.showInputDialog(null, "Select an experiment identifier", "Experiment Identifier", JOptionPane.PLAIN_MESSAGE, null, null, identifier );
		if (s==null) {
			// We quit if we have no eidentifier
			System.exit(0);
		}
		identifier = s;
		
	}

	@Override
	public String identifier() {
		return identifier;
	}

	public ExperimentIdentifier getExperimentIdentifier() {
		return experimentIdentifier;
	}

	public void setExperimentIdentifier(ExperimentIdentifier experimentIdentifier) {
		this.experimentIdentifier = experimentIdentifier;
	}
}
