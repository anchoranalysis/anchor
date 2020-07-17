package org.anchoranalysis.bean.init;

import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Routines for checking that particular patterns exist with bean-properties
 * <p>
 * They are intended to be used inside an overridden {@link org.anchoranalysis.bean.AnchorBean#checkMisconfigured) method.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class CheckMisconfigured {

    /**
     * Checks that one of two properties is defined, but not both simultaneously (exclusive OR).
     * 
     * @param property1Name name of first property (for error messages)
     * @param property2Name name of second property (for error messages)
     * @param property1Defined whether property1 is defined
     * @param property2Defined whether property2 is defined
     * @throws BeanMisconfiguredException if neither or both properties are defined
     */
    public static void oneOnly(String property1Name, String property2Name, boolean property1Defined, boolean property2Defined) throws BeanMisconfiguredException {
        if (!property1Defined && !property2Defined) {
            throw new BeanMisconfiguredException(
                String.format("One of either '%s' or '%s' must be defined", property1Name, property2Name)
            );
        }
        if (property1Defined && property2Defined) {
            throw new BeanMisconfiguredException(
                String.format("Only of both '%s' and '%s' should be defined", property1Name, property2Name)
            );
        }
    }
}
