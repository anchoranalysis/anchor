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
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.session.calculator.single.FeatureCalculatorSingle;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.bean.init.MarksInitialization;
import org.anchoranalysis.mpp.bean.provider.SingleMarkProvider;
import org.anchoranalysis.mpp.feature.bean.mark.FeatureInputMark;
import org.anchoranalysis.mpp.mark.Mark;

public class ReportFeatureOnMark extends ReportFeatureForMarks<FeatureInputMark> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private SingleMarkProvider markProvider;
    // END BEAN PROPERTIES

    @Override
    public boolean isNumeric() {
        return true;
    }

    @Override
    public String featureDescription(MarksInitialization param, Logger logger)
            throws OperationFailedException {

        // Maybe we should duplicate the providers?
        try {
            init(param, logger);
            markProvider.initRecursive(param, logger);
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

            Dimensions dimensions = createImageDim();

            double val =
                    session.calculate(new FeatureInputMark(mark.get(), Optional.of(dimensions)));
            return Double.toString(val);
        } catch (FeatureCalculationException | CreateException e) {
            throw new OperationFailedException(e);
        }
    }
}
