package org.anchoranalysis.experiment.bean.require;

public class RequireDebugMode extends RequireArguments {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean hasAllRequiredArguments( boolean debugModeEnabled ) {
		return debugModeEnabled;
	}
}
