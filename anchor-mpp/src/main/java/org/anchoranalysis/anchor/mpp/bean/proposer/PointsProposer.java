/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.proposer;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.anchor.mpp.mark.CompatibleWithMark;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.anchor.mpp.proposer.visualization.CreateProposalVisualization;
import org.anchoranalysis.bean.NullParamsBean;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageDimensions;

public abstract class PointsProposer extends NullParamsBean<PointsProposer>
        implements CompatibleWithMark {

    public abstract Optional<List<Point3i>> propose(
            Point3d point,
            Mark mark,
            ImageDimensions dimensions,
            RandomNumberGenerator randomNumberGenerator,
            ErrorNode errorNode)
            throws ProposalAbnormalFailureException;

    public abstract Optional<CreateProposalVisualization> proposalVisualization(boolean detailed);
}
