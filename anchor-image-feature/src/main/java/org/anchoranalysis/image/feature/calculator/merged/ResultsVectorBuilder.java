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

package org.anchoranalysis.image.feature.calculator.merged;

import java.util.Optional;
import java.util.function.Function;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.results.ResultsVector;
import org.anchoranalysis.feature.session.calculator.multi.FeatureCalculatorMulti;
import org.anchoranalysis.image.feature.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.input.FeatureInputSingleObject;
import org.anchoranalysis.image.voxel.object.ObjectMask;

class ResultsVectorBuilder {

    private Optional<ErrorReporter> errorReporter;
    private ResultsVector out;
    private int count;

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
        this.count = 0;
    }

    /** Calculates and inserts a derived {@link ObjectMask} input from a merged input. */
    public void calculateAndInsert(
            FeatureInputPairObjects inputPair,
            Function<FeatureInputPairObjects, ObjectMask> extractObj,
            FeatureCalculatorMulti<FeatureInputSingleObject> calculator)
            throws NamedFeatureCalculateException {
        FeatureInputSingleObject inputSingle =
                new FeatureInputSingleObject(extractObj.apply(inputPair));
        calculateAndInsert(inputSingle, calculator);
    }

    /**
     * Calculates the parameters belong to a particular session and inserts into a ResultsVector
     *
     * @param input
     * @param calculator
     */
    public <T extends FeatureInput> void calculateAndInsert(
            T input, FeatureCalculatorMulti<T> calculator) throws NamedFeatureCalculateException {
        ResultsVector rvImage =
                errorReporter.isPresent()
                        ? calculator.calculateSuppressErrors(input, errorReporter.get())
                        : calculator.calculate(input);
        out.set(count, rvImage);
        count += rvImage.size();
    }

    public ResultsVector getResultsVector() {
        return out;
    }
}
