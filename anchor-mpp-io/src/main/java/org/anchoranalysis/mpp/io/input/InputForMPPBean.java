package org.anchoranalysis.mpp.io.input;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.init.ImageInitParams;

/** The input can be used to initialize an MPP Bean */
public interface InputForMPPBean {

	void addToSharedObjects( MPPInitParams soMPP, ImageInitParams soImage ) throws OperationFailedException;
}
