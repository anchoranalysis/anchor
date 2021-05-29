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

package org.anchoranalysis.mpp.bean;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.bean.initializable.InitializableBean;
import org.anchoranalysis.bean.initializable.property.ExtractFromParam;
import org.anchoranalysis.bean.initializable.property.PropertyInitializer;
import org.anchoranalysis.bean.initializable.property.SimplePropertyDefiner;
import org.anchoranalysis.bean.shared.dictionary.DictionaryInitialization;
import org.anchoranalysis.feature.shared.FeaturesInitialization;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
import org.anchoranalysis.mpp.bean.init.MarksInitialization;
import org.anchoranalysis.mpp.bean.init.PointsInitialization;

public abstract class MarksBean<T> extends InitializableBean<T, MarksInitialization> {

    protected MarksBean() {
        super(initializerForMarksBeans(), new SimplePropertyDefiner<>(MarksInitialization.class));
    }

    /**
     * Creates a property-initializes for MPP-Beans
     *
     * <p>Beware concurrency. Initializers are stateful with the {#link {@link
     * PropertyInitializer#setParam(Object)} method so this should be created newly for each thread,
     * rather reused statically
     *
     * @return
     */
    public static PropertyInitializer<MarksInitialization> initializerForMarksBeans() {
        return new PropertyInitializer<>(MarksInitialization.class, paramExtracters());
    }

    private static List<ExtractFromParam<MarksInitialization, ?>> paramExtracters() {
        return Arrays.asList(
                new ExtractFromParam<>(PointsInitialization.class, MarksInitialization::points),
                new ExtractFromParam<>(FeaturesInitialization.class, MarksInitialization::feature),
                new ExtractFromParam<>(
                        DictionaryInitialization.class, MarksInitialization::dictionary),
                new ExtractFromParam<>(ImageInitialization.class, MarksInitialization::image));
    }
}
