/* (C)2020 */
package org.anchoranalysis.image.feature.object.input;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.anchoranalysis.feature.input.FeatureInputNRG;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * An input representing a single object-mask (with maybe an NRG-stack associated)
 *
 * <p>Equals and hash-code must be sensibly defined as these inputs can be used as keys in a cache.
 * The equals implementation assumes equals of {@link ObjectMask} is shallow and computationally
 * inexpensive..
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode(callSuper = true)
public class FeatureInputSingleObject extends FeatureInputNRG {

    @Getter private ObjectMask object;

    public FeatureInputSingleObject(ObjectMask object) {
        this(object, Optional.empty());
    }

    public FeatureInputSingleObject(ObjectMask object, NRGStackWithParams nrgStack) {
        this(object, Optional.of(nrgStack));
    }

    public FeatureInputSingleObject(ObjectMask object, Optional<NRGStackWithParams> nrgStack) {
        super(nrgStack);
        this.object = object;
    }

    @Override
    public String toString() {
        return object.toString();
    }
}
