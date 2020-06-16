package org.anchoranalysis.annotation.io.assignment;

import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.core.text.TypedValue;

/** 
 * Wraps a <pre>List<TypedValue></pre> to add floating point precision to doubles
 **/
class WrappedTypeValueList {

	private final int numDecimalPlaces;

	private final List<TypedValue> delegate = new ArrayList<>();
	
	/**
	 * Constructor
	 * 
	 * @param numDecimalPlaces the numer of decimals places to use when double values are added
	 */
	public WrappedTypeValueList(int numDecimalPlaces) {
		super();
		this.numDecimalPlaces = numDecimalPlaces;
	}		
			
	/** Adds one or more integer values */
	public void add(int ...val) {
		for(int v : val) {
			delegate.add(
				new TypedValue(v)
			);
		}
	}
	
	/** Adds one or more doubles, with two decimal places */
	public void add(double ...val) {
		for(double v : val) {
			delegate.add(
				new TypedValue(v, numDecimalPlaces)
			);
		}
	}

	public List<TypedValue> asList() {
		return delegate;
	}
}