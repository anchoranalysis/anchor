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

import org.anchoranalysis.feature.calc.FeatureCalculationException;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;

public class ReportFeatureOnObject extends ReportFeatureOnObjectsBase<FeatureInputSingleObject> {

    @Override
    protected double calcFeatureOn(
            ObjectCollection objects, FeatureCalculatorSingle<FeatureInputSingleObject> session)
            throws FeatureCalculationException {
        return session.calc(new FeatureInputSingleObject(extractObjFromCollection(objects)));
    }

    private ObjectMask extractObjFromCollection(ObjectCollection objects)
            throws FeatureCalculationException {
        if (objects.size() == 0) {
            throw new FeatureCalculationException("No object found");
        }
        if (objects.size() > 1) {
            throw new FeatureCalculationException("More than one object found");
        }
        return objects.get(0);
    }
}
