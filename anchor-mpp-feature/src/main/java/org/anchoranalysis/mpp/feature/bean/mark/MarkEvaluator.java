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

package org.anchoranalysis.mpp.feature.bean.mark;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.feature.bean.FeatureRelatedBean;
import org.anchoranalysis.mpp.bean.mark.factory.MarkWithIdentifierFactory;
import org.anchoranalysis.mpp.feature.bean.energy.scheme.EnergySchemeCreator;

/**
 * A bean for evaluating marks using features and energy schemes.
 *
 * <p>This class extends {@link FeatureRelatedBean} to provide functionality related to feature
 * evaluation for marks in the context of Marked Point Processes (MPP).
 */
public class MarkEvaluator extends FeatureRelatedBean<MarkEvaluator> {

    // START BEAN PROPERTIES
    /** Factory for creating marks with identifiers. */
    @BeanField @Getter @Setter private MarkWithIdentifierFactory markFactory;

    /** Defines additional configuration for the evaluation process. */
    @BeanField @Getter @Setter private Define define;

    /** Creator for the energy scheme used in mark evaluation. */
    @BeanField @Getter @Setter private EnergySchemeCreator energySchemeCreator;
    // END BEAN PROPERTIES
}
