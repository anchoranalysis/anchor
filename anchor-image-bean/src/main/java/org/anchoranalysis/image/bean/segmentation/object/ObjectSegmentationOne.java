package org.anchoranalysis.image.bean.segmentation.object;

import java.util.Optional;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.bean.nonbean.error.SgmnFailedException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.seed.SeedCollection;

public abstract class ObjectSegmentationOne extends ObjectSegmentation {

	// START BEAN PROPERTIES
	@BeanField
	private ObjectSegmentation sgmn;
	// END BEAN PROPERTIES
	
	@Override
	public ObjectCollection sgmn(Channel chnl, Optional<ObjectMask> mask, Optional<SeedCollection> seeds)
			throws SgmnFailedException {
		return sgmn(chnl, mask, seeds, sgmn);
	}
	
	protected abstract ObjectCollection sgmn(
		Channel chnl,
		Optional<ObjectMask> objMask,
		Optional<SeedCollection> seeds,
		ObjectSegmentation sgmn
	) throws SgmnFailedException;
	
	public ObjectSegmentation getSgmn() {
		return sgmn;
	}

	public void setSgmn(ObjectSegmentation sgmn) {
		this.sgmn = sgmn;
	}
}
