package org.anchoranalysis.core.error.friendly;

import java.util.Iterator;

/**
 * Iterates over an exception and all it's causes skipping friendly-exceptions with empty messages 
 * 
 * <p>Note it won't skip such an exception if it is the final one in the chain</p>
 * 
 * @author owen
 *
 */
class ExceptionToPrintIterator implements Iterator<Throwable> {

	private Throwable current;
	
	public ExceptionToPrintIterator(Throwable root) {
		this.current = skipIfNotAcceptable( root );
	}
	
	@Override
	public boolean hasNext() {
		return !ExceptionTypes.isFinal(current);
	}

	@Override
	public Throwable next() {
		current = skipIfNotAcceptable( current.getCause() );
		return current;
	}
	
	private static Throwable skipIfNotAcceptable( Throwable e ) {
		
		// Skip any exception with any empty message if it's an AnchorFriendlyCheckedException and not final
		// The loop is guaranteed to end, as we'll certainly eventually meet a final exception
		while (ExceptionTypes.hasEmptyMessage(e) && ExceptionTypes.isFriendly(e) && !ExceptionTypes.isFinal(e)) {
			e = e.getCause();
		}
		
		return e;
	}
}