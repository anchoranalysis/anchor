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

package org.anchoranalysis.image.feature.calculator;

import java.util.Optional;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorMulti;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;

/**
 * A feature-calculator with additional functions for encoding the output in a tabular format with
 * column names.
 *
 * <p>This interface extends FeatureCalculatorMulti to provide methods for initializing,
 * duplicating, and naming features in a tabular structure.
 *
 * @param <T> the type of feature input
 * @author Owen Feehan
 */
public interface FeatureTableCalculator<T extends FeatureInput> extends FeatureCalculatorMulti<T> {

    /**
     * Initializes a feature store that has the same structure as that previously created by
     * createFeatures() from the same object.
     *
     * @param initialization the image initialization context
     * @param energyStack an optional energy stack for feature calculation
     * @param logger a logger for reporting initialization progress or errors
     * @throws InitializeException if initialization fails
     */
    void start(ImageInitialization initialization, Optional<EnergyStack> energyStack, Logger logger)
            throws InitializeException;

    /**
     * Makes a copy of the feature-store for a new thread.
     *
     * <p>Deep-copies the features. Shallow-copies everything else.
     *
     * @return the copied feature-store
     */
    FeatureTableCalculator<T> duplicateForNewThread();

    /**
     * Creates a list of names for each feature (columns of the table).
     *
     * @return the list of feature names
     */
    FeatureNameList createFeatureNames();
}
