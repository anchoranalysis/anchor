/* (C)2020 */
package org.anchoranalysis.experiment.bean.identifier;

import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;

public abstract class ExperimentIdentifier extends AnchorBean<ExperimentIdentifier> {

    /**
     * Creates an identifier for the experiment
     *
     * @param taskName a name describing the current task if it xists
     * @return a string to identify the current experiment
     */
    public abstract String identifier(Optional<String> taskName);
}
