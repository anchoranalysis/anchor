package org.anchoranalysis.mpp.sgmn.define;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Directories into which collections of different types of objects are written to in a <emph>define</emph> experiment
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class OutputterDirectories {
	
	/** Where collections of objects are typically placed in a define-experiment output */
	public static final String OBJECT = "objects";

	/** Where collections of histograms are typically placed in a define-experiment output */
	public static final String HISTOGRAM = "histogramCollection";

	/** Where collections of marks are typically placed in a define-experiment output */
	public static final String CFG = "cfgCollection";
}
