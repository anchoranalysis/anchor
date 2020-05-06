package org.anchoranalysis.feature.cache;

/*-
 * #%L
 * anchor-feature
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

/** 
 * A unique identifier for a child-cache name, that uses a class and optionally additionally a part-name
 * <ul>
 * a class -> guaranteed to be unique for the class
 * an option part-name (string) -> a further division of the class into different caches
 * </ul>
 * 
 **/
public class ChildCacheName {

	private Class<?> cls;
	private String part;
	
	/**
	 * Uses only the class as an identifier - and a blank part-name
	 * 
	 * @param cls class
	 */
	public ChildCacheName(Class<?> cls) {
		this(cls,"");
	}
	
	/**
	 * Uses only the class as an identifier - and a integer part-name
	 * 
	 * @param cls class
	 */
	public ChildCacheName(Class<?> cls, int id) {
		this(
			cls,
			String.valueOf(id)
		);
	}
	
	/**
	 * Uses both the class and a part-name as an identifier
	 * 
	 * @param cls class
	 * @param part part-name
	 */
	public ChildCacheName(Class<?> cls, String part) {
		super();
		this.cls = cls;
		this.part = part;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cls == null) ? 0 : cls.getCanonicalName().hashCode());
		result = prime * result + ((part == null) ? 0 : part.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChildCacheName other = (ChildCacheName) obj;
		if (cls == null) {
			if (other.cls != null)
				return false;
		} else if (!cls.getCanonicalName().equals(other.cls.getCanonicalName()))
			return false;
		if (part == null) {
			if (other.part != null)
				return false;
		} else if (!part.equals(other.part))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s:%s", cls.getCanonicalName(), part);
	}
	
	
}
