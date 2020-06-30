package org.anchoranalysis.anchor.mpp.bean.proposer;

import java.util.Optional;

import org.anchoranalysis.anchor.mpp.bean.MPPBean;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.params.ICompatibleWith;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.orientation.Orientation;

public abstract class OrientationProposer extends MPPBean<OrientationProposer> implements ICompatibleWith {

	public abstract Optional<Orientation> propose( Mark mark, ImageDimensions dim, RandomNumberGenerator re ) throws ProposalAbnormalFailureException;
}
