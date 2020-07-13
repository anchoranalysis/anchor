package org.anchoranalysis.experiment.bean.log;

import org.anchoranalysis.bean.annotation.BeanField;

import lombok.Getter;
import lombok.Setter;

public abstract class ToTextFileBase extends LoggingDestination {

	// START BEAN FIELDS
	/** The name to use (without extension) for the text-file log */
	@BeanField @Getter @Setter
	private String outputName = "log";
	// END BEAN FIELDS
}
