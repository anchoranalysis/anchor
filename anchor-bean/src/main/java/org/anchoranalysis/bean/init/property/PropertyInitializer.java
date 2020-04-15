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

import org.anchoranalysis.bean.init.InitializableBean;
import org.anchoranalysis.bean.init.params.NullInitParams;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;

/**
 * 
 * @author Owen Feehan
 *
 * @param <P> init-param type
 */
public class PropertyInitializer<P> {
	
	private P param;
	private Class<?> initParamType;
	
	public PropertyInitializer( Class<?> initParamType ) {
		super();
		this.initParamType = initParamType;
	}
	
	public void setParam(P param) {
		this.param = param;
	}
	
	public boolean execIfInheritsFrom( Object propertyValue, Object parent, LogErrorReporter logger ) throws InitException {
		
		if (param!=null) {
			if (initMatchingPropertiesWith(propertyValue, parent, logger, param.getClass(), getParam())) {
				return true;
			}
		}

		// We can always initialize anything that doesn't take parameters (i.e. takes NullInitParams)
		if (initMatchingPropertiesWith(propertyValue, parent, logger, NullInitParams.class, NullInitParams.instance())) {
			return true;
		}
		
		return false;
		
	}
	
	protected PropertyDefiner<?> findPropertyThatDefines( Object propertyValue, Class<?> paramType ) {
		
		if (propertyValue instanceof InitializableBean ) {
			
			InitializableBean<?,?> initBean = (InitializableBean<?,?>) propertyValue;
			PropertyDefiner<?> pd = initBean.getPropertyDefiner();
			
			if (pd.accepts(paramType)) {
				return pd;
			} else {
				return null;
			}
			
		} else {
			return null;
		}
	}

	public Class<?> getInitParamType() {
		return initParamType;
	}

	protected P getParam() {
		return param;
	}
	
	protected boolean initMatchingPropertiesWith(
		Object propertyValue,
		Object parent,
		LogErrorReporter logger,
		Class<?> propertyClassToMatch,
		Object paramToInitWith
	) throws InitException {
		PropertyDefiner<?> pd = findPropertyThatDefines( propertyValue, propertyClassToMatch );
		if (pd!=null) {
			pd.doInitFor( propertyValue, paramToInitWith, parent, logger );
			return true;
		}
		return false;
	}
}
