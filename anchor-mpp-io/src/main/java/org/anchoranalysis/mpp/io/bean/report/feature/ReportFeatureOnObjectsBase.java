/*-
 * #%L
 * anchor-mpp-io
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

package org.anchoranalysis.mpp.io.bean.report.feature;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.image.bean.provider.ObjectCollectionProvider;
import org.anchoranalysis.image.object.ObjectCollection;

public abstract class ReportFeatureOnObjectsBase<T extends FeatureInput>
        extends ReportFeatureEvaluator<T> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ObjectCollectionProvider objects;
    // END BEAN PROPERTIES

    @Override
    public String genFeatureStringFor(MPPInitParams so, Logger logger)
            throws OperationFailedException {
        try {
            objects.initRecursive(so.getImage(), logger);
            super.init(so, logger);
        } catch (InitException e) {
            throw new OperationFailedException(e);
        }

        try {
            FeatureCalculatorSingle<T> session = super.createAndStartSession();
            return Double.toString(calcFeatureOn(objects.create(), session));

        } catch (FeatureCalcException | CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    protected abstract double calcFeatureOn(
            ObjectCollection objects, FeatureCalculatorSingle<T> session)
            throws FeatureCalcException;

    @Override
    public boolean isNumeric() {
        return true;
    }
}
