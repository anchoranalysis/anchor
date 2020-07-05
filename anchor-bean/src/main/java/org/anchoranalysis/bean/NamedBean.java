package org.anchoranalysis.bean;

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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.name.value.NameValue;

/**
 * A bean with an associated (textual) name
 * 
 * @author Owen Feehan
 *
 * @param <T> item type
 */
public class NamedBean<T extends AnchorBean<?>> extends NullParamsBean<NamedBean<T>> implements NameValue<T> {

	// START BEAN PROPERTIES
	@BeanField
	private String name;
	
	@BeanField
	private T item;
	// END BEAN PROPERTIES

	/***
	 * Constructor - uses an empty-string as name, and a nullable-item. Needed for bean-construction.
	 */
	public NamedBean() {
		name = "";
		item = null;
	}
	
	/**
	 * Constructor with a specific name and item
	 * 
	 * @param name name
	 * @param item item
	 */
	public NamedBean( String name, T item ) {
		this.name = name;
		this.item = item;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public T getValue() {
		return item;
	}

	@Override
	public void setValue(T item) {
		this.item = item;
	}
	
	/** The item that is to be named (i.e. the underlying bean) */
	public T getItem() {
		return getValue();
	}
	
	/** {@link setItem} */
	public void setItem(T item) {
		setValue(item);
	}

	/** {@link getName} */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/** The name of the bean */
	@Override
	public String getName() {
		return name;
	}
}
