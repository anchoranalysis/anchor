package org.anchoranalysis.core.error.combinable;

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


import java.util.StringJoiner;

import org.anchoranalysis.core.error.AnchorCheckedException;

/**
 *  An exception that can be combined with similar exceptions and summarized.
 *  
 *  <p>A special type of exception where lots of nested exceptions of the same type
 *  can be combined together to create a new exception summarising them all together.</p>
 *   
 *  <p>This is useful for nested-exceptions which could be better expressed
 *     as a path e.g. /cause3/cause2/cause1/exception</p>
 *
 *  <p>It is also supported to skip certain exception types when traversing through
 *  the hierarchy of nested exceptions (i.e. getCause().getCause().getCause().</p>
 *  
 *  <p>When skipped, the exception is ignored, and its getCause() is then processed.</p>
 *  
 *  <p>Otherwise, if a getCause() is encountered that can be neither combined, nor
 *  skipped, the process ends.</p>
 *  
 *  <p>Finally, a {@link org.anchoranalysis.core.error.combinable.SummaryException} is created
 *  representing the combined beans. Its getCause() is set as the getCause() of the last bean
 *  to be combined.</p>
 * 
 * @author Owen Feehan
 *
 */
public abstract class AnchorCombinableException extends AnchorCheckedException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6966810405755062033L;
	
	private final String dscr;

	protected AnchorCombinableException(String dscr, Throwable cause) {
		super(dscr,cause);	// This message should never be seen, as we should always call the summarize functionality
		this.dscr = dscr;
	}
	
	protected abstract boolean canExceptionBeCombined( Throwable exc );
	
	protected abstract boolean canExceptionBeSkipped( Throwable exc );
	
	public abstract Throwable summarize();
	
	protected Throwable createCombinedException(String combinedNames, Throwable finalClause) {
		return new SummaryException(createMessageForDscr(combinedNames), finalClause );
	}
	
	/**
	 * Creates a message for the exception from the description
	 * 
	 * @param dscr either a single description, or a combined description
	 * @return a message describing an error, incorporating description
	 */
	protected abstract String createMessageForDscr( String dscr );
	
	
	private void joinException( Throwable exc, StringJoiner sj ) {
		AnchorCombinableException excCast = (AnchorCombinableException) exc;
		sj.add(excCast.getDscr());
	}
	
	
	/**
	 * Traverses through a set of nested-exceptions creating a description for
	 * each "combined" exception, and combining them into a single string.
	 * 
	 * @param prefix a string to be inserted initially
	 * @param seperator a seperator is placed between the description of each exception
	 * @return a string as above
	 */
	protected Throwable combineDscrsRecursively( String prefix, String seperator ) {
		
		StringJoiner sj = new StringJoiner(seperator,prefix,"");
		
		Throwable finalClause = this;
		Throwable e = this;
		do {
			if (canExceptionBeCombined(e)) {
				
				joinException(e, sj);
				
				finalClause = e.getCause();
				e = e.getCause();
				
			} else if (canExceptionBeSkipped(e)) { 		
				// We skip certain types of exceptions (basically ignore them, and move to the next cause)
				e = e.getCause();
				continue;
			} else {
				// If it's on neither or Combine-List or Skip-List we get out
				break;
			}
		} while(e!=null);

		return createCombinedException( sj.toString(), finalClause );
	}
	
	
	/**
	 * Traverses through the nested-exceptions and finds the most deep exception that is combinable
	 * using the rules about combining and skipping outlined in the class description
	 * 
	 * @return the depth-most exception that is found by this search
	 */
	protected Throwable findMostDeepCombinableException() {
		
		Throwable finalExc = this;
		Throwable e = this;
		do {
			if (canExceptionBeCombined(e)) {
			
				finalExc = e;
				e = e.getCause();
				
			} else if (canExceptionBeSkipped(e)) { 		
				// We skip certain types of exceptions (basically ignore them, and move to the next cause)
				e = e.getCause();
				continue;
			} else {
				// If it's on neither or Combine-List or Skip-List we get out
				break;
			}
		} while(e!=null);

		return finalExc;
	}
	
	
	/**
	 * Are there nested-exceptions (according to our traversal rules) that are combinable?
	 * 
	 * @return TRUE if there are, FALSE otherwise
	 */
	protected boolean hasNoCombinableNestedExceptions() {
		return findMostDeepCombinableException()==this;
	}
	

	public String getDscr() {
		return dscr;
	}
}