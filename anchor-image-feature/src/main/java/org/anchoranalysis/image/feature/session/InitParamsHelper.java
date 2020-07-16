/* (C)2020 */
package org.anchoranalysis.image.feature.session;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InitParamsHelper {

    public static FeatureInitParams createInitParams(
            Optional<SharedObjects> sharedObjects, Optional<NRGStackWithParams> nrgStack) {

        Optional<KeyValueParams> kvp = nrgStack.map(NRGStackWithParams::getParams);

        return new FeatureInitParams(
                kvp, nrgStack.map(NRGStackWithParams::getNrgStack), sharedObjects);
    }
}
