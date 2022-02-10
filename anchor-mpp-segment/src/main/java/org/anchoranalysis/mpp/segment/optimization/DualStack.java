/*-
 * #%L
 * anchor-mpp-sgmn
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

package org.anchoranalysis.mpp.segment.optimization;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.image.bean.displayer.StackDisplayer;
import org.anchoranalysis.image.core.stack.DisplayStack;

/** Stores an {@link EnergyStack} together with a background stack. */
@Value
@AllArgsConstructor
public class DualStack {

    /** The energy-stack */
    private EnergyStack energyStack;

    /** The background */
    private DisplayStack background;

    /**
     * Creates to infer a background from the energy-stack.
     *
     * @param energyStack the energy-stack.
     * @param displayer how to display the {@code energyStack}.
     * @throws CreateException if a display-stack cannot be derived.
     */
    public DualStack(EnergyStack energyStack, StackDisplayer displayer) throws CreateException {
        this(energyStack, displayer.deriveFrom(energyStack.withoutParameters().asStack()));
    }
}
