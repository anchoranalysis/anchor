package org.anchoranalysis.experiment.bean.require;

public class RequireDebugMode extends RequireArguments {
	
	@Override
	public boolean hasAllRequiredArguments( boolean debugModeEnabled ) {
		return debugModeEnabled;
	}
}
