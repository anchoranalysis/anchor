/*-
 * #%L
 * anchor-image-feature
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
/* (C)2020 */
package org.anchoranalysis.image.feature.session.merged;

import java.util.Optional;
import java.util.function.Function;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.object.ObjectMask;

class ResultsVectorBuilder {

    private Optional<ErrorReporter> errorReporter;
    private ResultsVector out;
    private int cnt;

    /**
     * Constructor
     *
     * @param size
     * @param errorReporter if-defined feature errors are logged here and not thrown as exceptions.
     *     if not-defined, exceptions are thrown
     */
    public ResultsVectorBuilder(int size, Optional<ErrorReporter> errorReporter) {
        super();
        this.errorReporter = errorReporter;
        this.out = new ResultsVector(size);
        this.cnt = 0;
    }

    /** Calculates and inserts a derived obj-mask params from a merged. */
    public void calcAndInsert(
            FeatureInputPairObjects inputPair,
            Function<FeatureInputPairObjects, ObjectMask> extractObj,
            FeatureCalculatorMulti<FeatureInputSingleObject> calc)
            throws FeatureCalcException {
        FeatureInputSingleObject inputSingle =
                new FeatureInputSingleObject(extractObj.apply(inputPair));
        calcAndInsert(inputSingle, calc);
    }

    /**
     * Calculates the parameters belong to a particular session and inserts into a ResultsVector
     *
     * @param input
     * @param calc
     * @throws FeatureCalcException
     */
    public <T extends FeatureInput> void calcAndInsert(T input, FeatureCalculatorMulti<T> calc)
            throws FeatureCalcException {
        ResultsVector rvImage =
                errorReporter.isPresent()
                        ? calc.calcSuppressErrors(input, errorReporter.get())
                        : calc.calc(input);
        out.set(cnt, rvImage);
        cnt += rvImage.length();
    }

    public ResultsVector getResultsVector() {
        return out;
    }
}
