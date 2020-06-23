package org.anchoranalysis.feature.io.csv.name;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

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
public class CombinedName implements MultiName {
	
	private static final String SEPARATOR = "/";
	
	/**
	 * The key used for aggregating
	 */
	private String directoryPart;
	
	/**
	 * All the names not used in aggregating joined togther
	 */
	private String filePart;
	
	/**
	 * All names combined together in a single string with a forward slash as seperator.
	 * 
	 * <p>The primaryName is used for aggregating. The rest are not.</p>
	 * 
	 * primaryName/secondaryName
	 */
	private String allTogether;
	
	public CombinedName( String directoryPart, String filePart ) {
		this.directoryPart = directoryPart;
		this.filePart = filePart;
		this.allTogether =  String.join(SEPARATOR, directoryPart, filePart);
	}
	
	@Override
	public int hashCode() {
		return allTogether.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CombinedName) {
			CombinedName objCast = (CombinedName) obj;
			return allTogether.equals(objCast.allTogether);
		} else {
			return false;
		}
	}

	@Override
	public Optional<String> directoryPart() {
		// The primary name is used for aggregation
		return Optional.of(directoryPart);
	}

	@Override
	public Iterator<String> iterator() {
		return Arrays.asList(directoryPart, filePart).iterator();
	}
	
	@Override
	public String filePart() {
		return filePart;
	}
	
	@Override
	public String toString() {
		return allTogether;
	}

	@Override
	public int compareTo(MultiName other) {
		
		if (other instanceof CombinedName) {
		
			CombinedName otherCast = (CombinedName) other;
			
			int cmp = directoryPart.compareTo(otherCast.directoryPart);
			
			if (cmp!=0) {
				return cmp;
			}
			
			return filePart.compareTo(otherCast.filePart);
		} else {
			return 1;
		}
	}
}
