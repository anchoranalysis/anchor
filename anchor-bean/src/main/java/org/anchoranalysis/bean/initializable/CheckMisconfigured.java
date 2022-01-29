/*-
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.anchoranalysis.bean.initializable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;

/**
 * Routines for checking that particular patterns exist with bean-properties.
 *
 * <p>They are intended to be used inside an overridden {@link
 * org.anchoranalysis.bean.AnchorBean#checkMisconfigured} method.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckMisconfigured {

    /**
     * Checks that one of two properties is defined, but not both simultaneously.
     *
     * <p>This imposes an <b>exclusive or</b> check.
     *
     * @param property1Name name of <b>first</b> property (for error messages).
     * @param property2Name name of <b>second</b> property (for error messages).
     * @param property1Defined whether the <b>first</b> property is defined.
     * @param property2Defined whether the <b>second</b> property is defined.
     * @throws BeanMisconfiguredException if neither or both properties are defined.
     */
    public static void oneOnly(
            String property1Name,
            String property2Name,
            boolean property1Defined,
            boolean property2Defined)
            throws BeanMisconfiguredException {
        if (!property1Defined && !property2Defined) {
            throw new BeanMisconfiguredException(
                    String.format(
                            "One of either '%s' or '%s' must be defined",
                            property1Name, property2Name));
        }
        if (property1Defined && property2Defined) {
            throw new BeanMisconfiguredException(
                    String.format(
                            "Only one of both '%s' and '%s' must be defined",
                            property1Name, property2Name));
        }
    }

    /**
     * Checks that one of two properties is defined, or both.
     *
     * <p>This imposes an <b>or</b> check.
     *
     * @param property1Name name of <b>first</b> property (for error messages).
     * @param property2Name name of <b>second</b> property (for error messages).
     * @param property1Defined whether the <b>first</b> property is defined.
     * @param property2Defined whether the <b>second</b> property is defined.
     * @throws BeanMisconfiguredException if neither are defined.
     */
    public static void atLeastOne(
            String property1Name,
            String property2Name,
            boolean property1Defined,
            boolean property2Defined)
            throws BeanMisconfiguredException {
        if (!property1Defined && !property2Defined) {
            throw new BeanMisconfiguredException(
                    String.format(
                            "One of either '%s' or '%s' must be defined",
                            property1Name, property2Name));
        }
    }
}
