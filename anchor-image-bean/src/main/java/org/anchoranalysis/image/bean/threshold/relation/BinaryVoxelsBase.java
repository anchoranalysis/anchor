/* (C)2020 */
package org.anchoranalysis.image.bean.threshold.relation;

import lombok.EqualsAndHashCode;
import org.anchoranalysis.bean.shared.relation.threshold.RelationToThreshold;

@EqualsAndHashCode(callSuper = false)
public abstract class BinaryVoxelsBase extends RelationToThreshold {

    // This is sufficient for all base-classes, as we can rely on them not being further
    // parameterized
    @Override
    public String uniqueName() {
        return getClass().getCanonicalName();
    }
}
