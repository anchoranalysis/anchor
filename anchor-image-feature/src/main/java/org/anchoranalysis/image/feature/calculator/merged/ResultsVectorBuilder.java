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
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorMulti;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.results.ResultsVector;
import org.anchoranalysis.image.feature.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.input.FeatureInputSingleObject;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * Builds a ResultsVector by calculating and inserting feature results.
 *
 * <p>This class provides methods to calculate features for single objects or pairs of objects and
 * insert the results into a ResultsVector.
 */
class ResultsVectorBuilder {

    private Optional<ErrorReporter> errorReporter;
    private ResultsVector vector;
    private int count;

    /**
     * Constructor for ResultsVectorBuilder.
     *
     * @param size The size of the ResultsVector to be built.
     * @param errorReporter If defined, feature errors are logged here and not thrown as exceptions.
     *     If not defined, exceptions are thrown.
     */
    public ResultsVectorBuilder(int size, Optional<ErrorReporter> errorReporter) {
        super();
        this.errorReporter = errorReporter;
        this.vector = new ResultsVector(size);
        this.count = 0;
    }

    /**
     * Calculates and inserts a derived {@link ObjectMask} input from a merged input.
     *
     * @param inputPair The input pair of objects.
     * @param extractObj A function to extract an ObjectMask from the input pair.
     * @param calculator The feature calculator for single objects.
     * @throws NamedFeatureCalculateException If a calculation error occurs.
     */
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
     * Calculates the features for a particular input and inserts the results into the
     * ResultsVector.
     *
     * @param <T> The type of feature input.
     * @param input The input for feature calculation.
     * @param calculator The feature calculator.
     * @throws NamedFeatureCalculateException If a calculation error occurs.
     */
    public <T extends FeatureInput> void calculateAndInsert(
            T input, FeatureCalculatorMulti<T> calculator) throws NamedFeatureCalculateException {
        ResultsVector vectorLocal =
                errorReporter.isPresent()
                        ? calculator.calculateSuppressErrors(input, errorReporter.get())
                        : calculator.calculate(input);
        vector.set(count, vectorLocal);
        count += vectorLocal.size();
    }

    /**
     * Gets the built ResultsVector.
     *
     * @return The ResultsVector containing all calculated feature results.
     */
    public ResultsVector getResultsVector() {
        return vector;
    }
}
