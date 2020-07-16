/* (C)2020 */
package org.anchoranalysis.bean.shared.relation;

import lombok.EqualsAndHashCode;
import org.anchoranalysis.core.relation.GreaterThan;
import org.anchoranalysis.core.relation.RelationToValue;

@EqualsAndHashCode(callSuper = true)
public class GreaterThanBean extends RelationBean {

    @Override
    public String toString() {
        return GreaterThan.class.getSimpleName();
    }

    @Override
    public RelationToValue create() {
        return new GreaterThan();
    }
}
