package org.anchoranalysis.annotation.io.wholeimage.findable;

import org.anchoranalysis.core.log.LogErrorReporter;

/**
 * A positive-result when an object is found
 * 
 * @author owen
 *
 * @param <T>
 */
public class Found<T> extends Findable<T> {

	private T obj;

	/**
	 * The found object.
	 * 
	 * @param obj
	 */
	public Found(T obj) {
		super();
		this.obj = obj;
	}

	public T getObj() {
		return obj;
	}

	@Override
	public boolean logIfFailure(String name, LogErrorReporter logErrorReporter) {
		return true;
	}

	@Override
	public T getOrNull() {
		return obj;
	}
}
