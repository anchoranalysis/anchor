package org.anchoranalysis.anchor.mpp.bean.mark.factory;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkPointList;

public class MarkPointListFactory extends MarkFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	// END BEAN PROPERTIES
	
	@Override
	public Mark create() {
		MarkPointList mark = new MarkPointList();
		return mark;
	}
}
