/* (C)2020 */
package org.anchoranalysis.bean.shared.relation.threshold;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.NonNegative;
import org.anchoranalysis.bean.shared.relation.RelationBean;
import org.anchoranalysis.core.relation.RelationToValue;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RelationToConstant extends RelationToThreshold {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private RelationBean relation;

    @BeanField @Getter @Setter @NonNegative private double threshold = -1;
    // END BEAN PROPERTIES

    @Override
    public double threshold() {
        return threshold;
    }

    @Override
    public RelationToValue relation() {
        return relation.create();
    }

    @Override
    public String toString() {
        return String.format("%s %f", relation, threshold);
    }

    @Override
    public String uniqueName() {
        return String.format(
                "%s_%s_%d", getClass().getCanonicalName(), relation.uniqueName(), threshold);
    }
}
