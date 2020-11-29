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

package org.anchoranalysis.mpp.bean.init;

import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.initializable.params.BeanInitParams;
import org.anchoranalysis.bean.initializable.property.PropertyInitializer;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.bean.nonbean.init.PopulateStoreFromDefine;
import org.anchoranalysis.mpp.bean.points.fitter.PointsFitter;

public class PointsInitParams implements BeanInitParams {

    // START: InitParams
    private ImageInitParams soImage;
    // END: InitParams

    // START: Stores
    private NamedProviderStore<PointsFitter> storePointsFitter;
    // END: Stores

    private PointsInitParams(ImageInitParams soImage, SharedObjects so) {
        super();
        this.soImage = soImage;

        storePointsFitter = so.getOrCreate(PointsFitter.class);
    }

    public static PointsInitParams create(ImageInitParams soImage, SharedObjects so) {
        return new PointsInitParams(soImage, so);
    }

    public NamedProviderStore<PointsFitter> getPointsFitterSet() {
        return storePointsFitter;
    }

    public void populate(PropertyInitializer<?> pi, Define define, Logger logger)
            throws OperationFailedException {

        PopulateStoreFromDefine<PointsInitParams> populater =
                new PopulateStoreFromDefine<>(define, pi, logger);
        populater.copyInit(PointsFitter.class, storePointsFitter);
    }

    public ImageInitParams getImage() {
        return soImage;
    }
}
