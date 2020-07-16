/* (C)2020 */
package org.anchoranalysis.io.manifest;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;

public class ManifestExperimentInput extends AnchorBean<ManifestExperimentInput> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String file;
    // END BEAN PROPERTIES
}
