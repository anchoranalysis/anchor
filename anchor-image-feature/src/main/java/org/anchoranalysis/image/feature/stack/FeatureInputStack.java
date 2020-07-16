/* (C)2020 */
package org.anchoranalysis.image.feature.stack;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.feature.input.FeatureInputNRG;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

@EqualsAndHashCode(callSuper = true)
public class FeatureInputStack extends FeatureInputNRG {

    /**
     * Should only be used if it's guaranteed the NRG stack will be added later, as this this
     * required.
     */
    public FeatureInputStack() {
        this.setNrgStack(Optional.empty());
    }

    /**
     * Params without any ImageInitParams
     *
     * @param nrgStack
     */
    public FeatureInputStack(NRGStack nrgStack) {
        this.setNrgStack(new NRGStackWithParams(nrgStack));
    }

    /**
     * Params without any ImageInitParams
     *
     * @param nrgStack
     */
    public FeatureInputStack(Optional<NRGStack> nrgStack) {
        if (nrgStack.isPresent()) {
            this.setNrgStack(new NRGStackWithParams(nrgStack.get()));
        }
    }
}
