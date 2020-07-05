package org.anchoranalysis.experiment.task;

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

import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.io.input.InputFromManager;


/**
 * A list of base (parent) classes for input-types that a task expects
 * 
 * @author Owen Feehan
 *
 */
public class InputTypesExpected {

	private List<Class<? extends InputFromManager>> list = new ArrayList<>();
	
	public InputTypesExpected( Class<? extends InputFromManager> cls ) {
		list.add(cls);
	}
	
	/**
	 * Does the childclass inherit from any one of expected input-types
	 * 
	 * @param childClass the class to test if it inherits
	 * @return true if inherits from at least one input-type, false otherwise
	 */
	public boolean doesClassInheritFromAny( Class<? extends InputFromManager> childClass ) {
		for (Class<? extends InputFromManager> possibleInputTypeClass : list) {
			if (possibleInputTypeClass.isAssignableFrom(childClass)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean add(Class<? extends InputFromManager> e) {
		return list.add(e);
	}

	@Override
	public String toString() {
		// Describe the classes in the list
		return list.toString();
	}

}
