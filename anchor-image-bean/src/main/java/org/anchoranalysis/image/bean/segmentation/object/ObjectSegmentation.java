package org.anchoranalysis.image.bean.segmentation.object;

import java.util.Optional;

import org.anchoranalysis.image.bean.nonbean.error.SgmnFailedException;
import org.anchoranalysis.image.bean.segmentation.SegmentationBean;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.seed.SeedCollection;

public abstract class ObjectSegmentation extends SegmentationBean<ObjectSegmentation> {

	public abstract ObjectCollection sgmn( Channel chnl, Optional<ObjectMask> mask, Optional<SeedCollection> seeds ) throws SgmnFailedException;
}
