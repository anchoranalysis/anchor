package org.anchoranalysis.image.bean.sgmn.objmask;

import java.util.Optional;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.objectmask.ObjectCollection;
import org.anchoranalysis.image.seed.SeedCollection;
import org.anchoranalysis.image.sgmn.SgmnFailedException;

public abstract class ObjMaskSgmnOne extends ObjMaskSgmn {

	// START BEAN PROPERTIES
	@BeanField
	private ObjMaskSgmn sgmn;
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
		ObjMaskSgmn sgmn
	) throws SgmnFailedException;
	
	public ObjMaskSgmn getSgmn() {
		return sgmn;
	}

	public void setSgmn(ObjMaskSgmn sgmn) {
		this.sgmn = sgmn;
	}
}
