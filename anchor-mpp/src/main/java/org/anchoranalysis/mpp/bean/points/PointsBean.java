/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.bean.points;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.bean.initializable.InitializableBean;
import org.anchoranalysis.bean.initializable.property.AssignerMatchClass;
import org.anchoranalysis.bean.initializable.property.BeanInitializer;
import org.anchoranalysis.bean.initializable.property.ExtractDerivedParameter;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
import org.anchoranalysis.mpp.init.PointsInitialization;

/**
 * An abstract base class for beans that work with points and require initialization.
 *
 * @author Owen Feehan
 * @param <T> the specific type of the bean extending this class
 */
public abstract class PointsBean<T> extends InitializableBean<T, PointsInitialization> {

    /**
     * Constructs a new PointsBean. Initializes the bean with a BeanInitializer for
     * PointsInitialization and an AssignerMatchClass.
     */
    protected PointsBean() {
        super(
                new BeanInitializer<>(PointsInitialization.class, paramExtracters()),
                new AssignerMatchClass<>(PointsInitialization.class));
    }

    /**
     * Creates a list of parameter extractors for PointsInitialization.
     *
     * @return a list containing an ExtractDerivedParameter for ImageInitialization
     */
    private static List<ExtractDerivedParameter<PointsInitialization, ?>> paramExtracters() {
        return Arrays.asList(
                new ExtractDerivedParameter<>(
                        ImageInitialization.class, PointsInitialization::getImage));
    }
}
