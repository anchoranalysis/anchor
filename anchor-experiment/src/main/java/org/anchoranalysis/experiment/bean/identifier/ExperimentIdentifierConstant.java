package org.anchoranalysis.experiment.bean.identifier;

import java.util.Optional;

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


import org.anchoranalysis.bean.annotation.BeanField;


/**
 * Defines constants for name and version of an experiment
 * 
 * @author Owen Feehan
 *
 */
public class ExperimentIdentifierConstant extends ExperimentIdentifier {
	
	// START BEAN PROPERTIES
	@BeanField
	private String name;
	
	@BeanField
	private String version;
	// END BEAN PROPERTIES
	
	public ExperimentIdentifierConstant() {
		super();
	}
	
	public ExperimentIdentifierConstant(String name, String version) {
		super();
		this.name = name;
		this.version = version;
	}
	
	@Override
	public String identifier(Optional<String> taskName) {
		return IdentifierUtilities.identifierFromNameVersion(
			name,
			Optional.of(version)
		);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
}
