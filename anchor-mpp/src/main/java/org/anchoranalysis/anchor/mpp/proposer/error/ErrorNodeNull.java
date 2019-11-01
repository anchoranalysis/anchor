package org.anchoranalysis.anchor.mpp.proposer.error;

import org.anchoranalysis.anchor.mpp.mark.Mark;

public class ErrorNodeNull extends ErrorNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5512508477564211253L;

	@Override
	public ErrorNode add(String errorMessage) {
		// do nothing
		return this;
	}

	@Override
	public ErrorNode add(String errorMessage, Mark mark) {
		return this;
	}

	@Override
	public ErrorNode addFormatted(String formatString, Object... args) {
		return this;
	}

	@Override
	public ErrorNode addIter(int i) {
		return this;
	}

	@Override
	public ErrorNode addBean(String propertyName, Object object) {
		return this;
	}

	@Override
	public ErrorNode add(Exception e) {
		return this;
	}

	@Override
	public void addErrorDescription(StringBuilder sb) {
		
	}
	

}