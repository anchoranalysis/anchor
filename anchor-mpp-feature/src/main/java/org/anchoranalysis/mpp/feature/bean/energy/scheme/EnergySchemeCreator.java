/*-
 * #%L
 * anchor-mpp-feature
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

package org.anchoranalysis.mpp.feature.bean.energy.scheme;

import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.feature.bean.FeatureRelatedBean;
import org.anchoranalysis.mpp.feature.energy.scheme.EnergyScheme;

/**
 * An abstract class for creating {@link EnergyScheme} instances.
 * 
 * <p>This class extends {@link FeatureRelatedBean} to provide feature-related functionality
 * and defines an abstract method for creating {@link EnergyScheme} objects.</p>
 */
public abstract class EnergySchemeCreator extends FeatureRelatedBean<EnergySchemeCreator> {

    /**
     * Creates an {@link EnergyScheme} instance.
     *
     * @return a new {@link EnergyScheme} object
     * @throws CreateException if there's an error during the creation process
     */
    public abstract EnergyScheme create() throws CreateException;
}