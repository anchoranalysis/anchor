package org.anchoranalysis.core.name;

/*
 * #%L
 * anchor-core
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

/**
 * A name which is unique when a primaryName is combined with a secondaryName
 * 
 * In some cases (e.g. when grouping) the primaryName has relevance.
 * 
 * @author Owen
 *
 */
public class CombinedName implements MultiName, Comparable<CombinedName> {
	
	private String primaryName;
	private String secondaryName;
	
	/**
	 * Both names combined together in a single string with a forward slash as seperator.
	 * 
	 * primaryName/secondaryName
	 */
	private String together;
	
	public CombinedName( String primaryName, String secondaryName ) {
		this.primaryName = primaryName;
		this.secondaryName = secondaryName;
		this.together =  primaryName + "/" + secondaryName;
	}

	public String getPrimaryName() {
		return primaryName;
	}

	public String getSecondaryName() {
		return secondaryName;
	}

	public String getCombinedName() {
		return together;
	}

	@Override
	public int hashCode() {
		return together.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CombinedName) {
			CombinedName objCast = (CombinedName) obj;
			return together.equals(objCast.together);
		} else {
			return false;
		}
	}

	@Override
	public int numParts() {
		return 2;
	}

	@Override
	public String getPart(int index) {
		switch(index) {
		case 0:
			return primaryName;
		case 1:
			return secondaryName;
		default:
			assert false;
			return "";
		}
	}

	@Override
	public String getUniqueName() {
		return together;
	}
	
	@Override
	public String toString() {
		return getUniqueName();
	}

	@Override
	public int compareTo(CombinedName o) {
		int cmp = primaryName.compareTo(o.primaryName);
		
		if (cmp!=0) {
			return cmp;
		}
		return secondaryName.compareTo(o.secondaryName);
	}
}
