package org.anchoranalysis.bean.permute.property;

import org.anchoranalysis.bean.annotation.BeanField;

/**
 * Base class for permute-properties involving a sequence of numbers
 * 
 * @author owen
 *
 * @param <T>
 */
public abstract class PermutePropertySequence<T> extends PermutePropertyWithPath<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	/**
	 * Where the sequence starts
	 */
	@BeanField
	private int start = 0;
	
	/**
	 * How much to increment by
	 */
	@BeanField
	private int increment = 1;

	/**
	 * The final value. Equal to this value is included. Anything higher is not allowed. 
	 */
	@BeanField
	private int end = 1;
	// END BEAN PROPERTIES
	
	protected IntegerRange range() {
		return new IntegerRange(start,end,increment);
	}
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
	}
}
