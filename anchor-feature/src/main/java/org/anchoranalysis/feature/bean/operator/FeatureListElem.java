/* (C)2020 */
package org.anchoranalysis.feature.bean.operator;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.descriptor.FeatureInputType;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class FeatureListElem<T extends FeatureInput> extends Feature<T> {

    // START BEAN PARAMETERS
    @BeanField @Getter private List<Feature<T>> list = new ArrayList<>();
    // END BEAN PARAMETERS

    /**
     * Constructor
     *
     * @param featureList feature-list
     */
    protected FeatureListElem(FeatureList<T> featureList) {
        this.list = featureList.asList();
    }

    /**
     * A string description of all the items of the list concatenated together with a character in
     * between
     *
     * @param list
     * @param operatorDscr
     * @return
     */
    protected String descriptionForList(String operatorDscr) {
        return String.join(operatorDscr, FunctionalList.mapToList(list, Feature::getDscrLong));
    }

    public void setList(List<Feature<T>> list) {
        this.list = list;
    }

    public void setList(FeatureList<T> list) {
        this.list = list.asList();
    }

    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputType.determineInputType(list);
    }
}
