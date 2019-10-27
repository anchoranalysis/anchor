package org.anchoranalysis.bean.xml.error;

/*
 * #%L
 * anchor-bean
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


import org.apache.commons.configuration.ConfigurationRuntimeException;

/**
 * A bean's XML is thrown when a bean has misconfigured XML
 * 
 * As these exceptions tend to get nested inside each other, we need to
 *   eventually combine them, so that only the final-most errored bean is displayed
 *   to the user
 * 
 * @author Owen Feehan
 *
 */
public class BeanMisconfiguredXmlException extends BeanCombinableException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 * 
	 * @param msg the message we want to display to the user about the exception
	 * @param cause what caused it
	 */
	public BeanMisconfiguredXmlException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	/**
	 * Constructor
	 * 
	 * @param cause what caused it
	 */
	public BeanMisconfiguredXmlException(Throwable cause) {
		super("",cause);
	}

	@Override
	protected boolean canExceptionBeCombined( Throwable exc ) {
		return exc instanceof BeanMisconfiguredXmlException;
	}
	
	/**
	 * This summarize() option just looks for the most deep exception
	 * that can be 'combined' and takes its message
	 * 
	 * @return an exception that summarizes this exception (and maybe some nested-exceptions)
	 */
	@Override
	public Throwable summarize() {
		return super.findMostDeepCombinableException();
	}
	
	@Override
	protected boolean canExceptionBeSkipped(Throwable exc) {
		return exc instanceof ConfigurationRuntimeException;
	}
	
	

	@Override
	protected String createMessageForDscr(String dscr) {
		return dscr;
	}
}
