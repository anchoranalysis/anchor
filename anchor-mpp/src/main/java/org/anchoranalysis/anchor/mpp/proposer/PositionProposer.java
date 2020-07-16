/* (C)2020 */
package org.anchoranalysis.anchor.mpp.proposer;

import java.util.Optional;
import org.anchoranalysis.core.geometry.Point3d;

// Proposes a position
public interface PositionProposer {

    // Proposes a position, or NULL if there is no position to propose
    Optional<Point3d> propose(ProposerContext context);
}
