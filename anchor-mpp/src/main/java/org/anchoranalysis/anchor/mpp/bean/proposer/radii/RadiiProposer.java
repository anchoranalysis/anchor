/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.proposer.radii;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.bean.MPPBean;
import org.anchoranalysis.anchor.mpp.mark.CompatibleWithMark;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.orientation.Orientation;

public abstract class RadiiProposer extends MPPBean<RadiiProposer> implements CompatibleWithMark {

    // When we have no bounds, we should create bounds from the boundCalculator
    public abstract Optional<Point3d> propose(
            Point3d pos,
            RandomNumberGenerator randomNumberGenerator,
            ImageDimensions dimensions,
            Orientation orientation)
            throws ProposalAbnormalFailureException;
}
