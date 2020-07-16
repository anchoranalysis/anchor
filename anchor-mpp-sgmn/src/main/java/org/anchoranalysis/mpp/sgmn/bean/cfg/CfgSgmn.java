/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.cfg;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.image.bean.nonbean.error.SegmentationFailedException;
import org.anchoranalysis.image.experiment.identifiers.ImgStackIdentifiers;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.io.output.bound.BoundIOContext;

public abstract class CfgSgmn extends AnchorBean<CfgSgmn> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String backgroundStackName = ImgStackIdentifiers.INPUT_IMAGE;
    // END BEAN PROPERTIES

    // Creates state for the experiment in general, that is not tied to any particular image
    public abstract ExperimentState createExperimentState();

    public abstract Cfg sgmn(
            NamedImgStackCollection stacks,
            NamedProvider<ObjectCollection> objects,
            Optional<KeyValueParams> keyValueParams,
            BoundIOContext context)
            throws SegmentationFailedException;
}
