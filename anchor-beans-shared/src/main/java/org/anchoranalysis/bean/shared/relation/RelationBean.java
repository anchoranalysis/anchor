/* (C)2020 */
package org.anchoranalysis.bean.shared.relation;

import lombok.EqualsAndHashCode;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.GenerateUniqueParameterization;
import org.anchoranalysis.core.relation.RelationToValue;

@EqualsAndHashCode(callSuper = false)
public abstract class RelationBean extends AnchorBean<RelationBean>
        implements GenerateUniqueParameterization {

    public abstract RelationToValue create();

    @Override
    public abstract String toString();

    // This is sufficient for all base-classes, as we rely on them not being further parameterized
    @Override
    public String uniqueName() {
        return getClass().getCanonicalName();
    }
}
