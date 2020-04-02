package org.anchoranalysis.core.error.friendly;

/**
 * Categorizes different types of exceptions
 * 
 * @author owen
 *
 */
class ExceptionTypes {

	private ExceptionTypes() {
		
	}
	
	/** Is it the last exception in the chain? */
	public static boolean isFinal( Throwable exc ) {
		return exc.getCause()==null || exc.getCause()==exc;
	}
	
	/** Is it the last exception in the chain? */
	public static boolean isFriendly( Throwable exc ) {
		return exc instanceof IFriendlyException;
	}
		
	/** Does the exception lack a message? */
	public static boolean hasEmptyMessage( Throwable exc ) {
		return exc.getMessage()==null || exc.getMessage().isEmpty();
	}
}
