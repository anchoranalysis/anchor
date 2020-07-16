/* (C)2020 */
package org.anchoranalysis.bean.shared.relation;

import lombok.EqualsAndHashCode;
import org.anchoranalysis.core.relation.LessThan;
import org.anchoranalysis.core.relation.RelationToValue;

@EqualsAndHashCode(callSuper = true)
public class LessThanBean extends RelationBean {

    @Override
    public String toString() {
        return LessThanBean.class.getSimpleName();
    }

    @Override
    public RelationToValue create() {
        return new LessThan();
    }
}
