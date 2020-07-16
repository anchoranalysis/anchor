/* (C)2020 */
package org.anchoranalysis.experiment.bean.log;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;

public abstract class ToTextFileBase extends LoggingDestination {

    // START BEAN FIELDS
    /** The name to use (without extension) for the text-file log */
    @BeanField @Getter @Setter private String outputName = "log";
    // END BEAN FIELDS
}
