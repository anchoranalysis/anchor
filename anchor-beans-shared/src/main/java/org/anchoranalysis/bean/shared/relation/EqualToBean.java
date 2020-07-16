/* (C)2020 */
package org.anchoranalysis.bean.shared.relation;

import lombok.EqualsAndHashCode;
import org.anchoranalysis.core.relation.EqualTo;
import org.anchoranalysis.core.relation.RelationToValue;

@EqualsAndHashCode(callSuper = true)
public class EqualToBean extends RelationBean {

    @Override
    public RelationToValue create() {
        return new EqualTo();
    }

    @Override
    public String toString() {
        return EqualToBean.class.getSimpleName();
    }
}
