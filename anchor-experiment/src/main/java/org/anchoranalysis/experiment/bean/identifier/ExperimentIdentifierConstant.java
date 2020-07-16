/* (C)2020 */
package org.anchoranalysis.experiment.bean.identifier;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;

/**
 * Defines constants for name and version of an experiment
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentIdentifierConstant extends ExperimentIdentifier {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String name;

    @BeanField @Getter @Setter private String version;
    // END BEAN PROPERTIES

    @Override
    public String identifier(Optional<String> taskName) {
        return IdentifierUtilities.identifierFromNameVersion(name, Optional.of(version));
    }
}
