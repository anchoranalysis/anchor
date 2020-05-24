package org.anchoranalysis.image.bean.sgmn.objmask;

import java.util.Optional;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.seed.SeedCollection;
import org.anchoranalysis.image.sgmn.SgmnFailedException;

public abstract class ObjMaskSgmnOne extends ObjMaskSgmn {

	// START BEAN PROPERTIES
	@BeanField
	private ObjMaskSgmn sgmn;
	// END BEAN PROPERTIES
	
	@Override
	public ObjMaskCollection sgmn(Chnl chnl, Optional<ObjMask> mask, Optional<SeedCollection> seeds)
			throws SgmnFailedException {
		return sgmn(chnl, mask, seeds, sgmn);
	}
	
	protected abstract ObjMaskCollection sgmn(
		Chnl chnl,
		Optional<ObjMask> objMask,
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
