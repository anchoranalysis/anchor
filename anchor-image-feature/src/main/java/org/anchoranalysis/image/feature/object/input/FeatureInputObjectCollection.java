/* (C)2020 */
package org.anchoranalysis.image.feature.object.input;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.anchoranalysis.feature.input.FeatureInputNRG;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.object.ObjectCollection;

@EqualsAndHashCode(callSuper = true)
public class FeatureInputObjectCollection extends FeatureInputNRG {

    @Getter private final ObjectCollection objects;

    public FeatureInputObjectCollection(ObjectCollection objects) {
        this.objects = objects;
    }

    public FeatureInputObjectCollection(
            ObjectCollection objects, Optional<NRGStackWithParams> nrgStack) {
        super(nrgStack);
        this.objects = objects;
    }
}
