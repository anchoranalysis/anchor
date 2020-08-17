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

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.anchor.mpp.bean.provider.MarkProvider;
import org.anchoranalysis.anchor.mpp.feature.bean.mark.FeatureInputMark;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.image.extent.ImageDimensions;

public class ReportFeatureOnMark extends ReportFeatureForMPP<FeatureInputMark> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private MarkProvider markProvider;
    // END BEAN PROPERTIES

    @Override
    public boolean isNumeric() {
        return true;
    }

    @Override
    public String featureDescription(MPPInitParams so, Logger logger)
            throws OperationFailedException {

        // Maybe we should duplicate the providers?
        try {
            init(so, logger);
            markProvider.initRecursive(so, logger);
        } catch (InitException e) {
            throw new OperationFailedException(e);
        }

        Optional<Mark> mark;
        try {
            mark = markProvider.create();
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }

        if (!mark.isPresent()) {
            return "no mark returned";
        }

        try {
            FeatureCalculatorSingle<FeatureInputMark> session = createAndStartSession();

            ImageDimensions dimensions = createImageDim();

            double val =
                    session.calculate(new FeatureInputMark(mark.get(), Optional.of(dimensions)));
            return Double.toString(val);
        } catch (FeatureCalculationException | CreateException e) {
            throw new OperationFailedException(e);
        }
    }
}
