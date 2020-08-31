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
import org.anchoranalysis.bean.initializable.property.ExtractFromParam;
import org.anchoranalysis.bean.initializable.property.PropertyInitializer;
import org.anchoranalysis.bean.initializable.property.SimplePropertyDefiner;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.mpp.bean.init.PointsInitParams;

/**
 * @author Owen Feehan
 * @param <T> bean-type
 */
public abstract class PointsBean<T> extends InitializableBean<T, PointsInitParams> {

    protected PointsBean() {
        super(
                new PropertyInitializer<>(PointsInitParams.class, paramExtracters()),
                new SimplePropertyDefiner<PointsInitParams>(PointsInitParams.class));
    }

    private static List<ExtractFromParam<PointsInitParams, ?>> paramExtracters() {
        return Arrays.asList(
                new ExtractFromParam<>(ImageInitParams.class, PointsInitParams::getImage));
    }
}