package org.anchoranalysis.bean.init.property;

/*-
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.bean.init.params.IInitParams;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @author Owen Feehan
 *
 * @param <P> param type
 */
@RequiredArgsConstructor
public class SimplePropertyDefiner<P> extends PropertyDefiner {

	private final Class<?> paramTypeMatch;

	@Override
	public boolean accepts(Class<?> paramType) {
		return paramTypeMatch.isAssignableFrom(paramType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doInitFor(Object propertyValue, Object param,
			Object parent, LogErrorReporter logger) throws InitException {
		assert propertyValue instanceof IInitParams;
		assert paramTypeMatch.isAssignableFrom( param.getClass() );
		
		IInitParams<P> propertyValueCast = (IInitParams<P>) propertyValue;
		propertyValueCast.init( (P) param, logger);
	}

	@Override
	public String toString() {
		return String.format("simpleProp=%s", paramTypeMatch);
	}

	@Override
	public String describeAcceptedClasses() {
		return paramTypeMatch.getSimpleName();
	}
}
