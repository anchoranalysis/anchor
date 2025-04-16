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

package org.anchoranalysis.mpp.init;

import lombok.Getter;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.initializable.parameters.BeanInitialization;
import org.anchoranalysis.bean.initializable.property.BeanInitializer;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
import org.anchoranalysis.image.bean.nonbean.init.PopulateStoreFromDefine;
import org.anchoranalysis.mpp.bean.points.fitter.PointsFitter;

/**
 * Initialization class for points-related components in the MPP framework.
 *
 * <p>This class manages the initialization of point fitters and provides access to image
 * initialization.
 */
public class PointsInitialization implements BeanInitialization {

    /** The image initialization associated with this points initialization. */
    @Getter private final ImageInitialization image;

    /** Store for point fitters. */
    @Getter private final NamedProviderStore<PointsFitter> pointFitters;

    /**
     * Private constructor for PointsInitialization.
     *
     * @param image The ImageInitialization to use
     * @param sharedObjects The SharedObjects to use for creating NamedProviderStores
     */
    private PointsInitialization(ImageInitialization image, SharedObjects sharedObjects) {
        this.image = image;
        pointFitters = sharedObjects.getOrCreate(PointsFitter.class);
    }

    /**
     * Creates a new PointsInitialization instance.
     *
     * @param image The ImageInitialization to use
     * @param so The SharedObjects to use for creating NamedProviderStores
     * @return A new PointsInitialization instance
     */
    public static PointsInitialization create(ImageInitialization image, SharedObjects so) {
        return new PointsInitialization(image, so);
    }

    /**
     * Populates the stores with beans from the given Define.
     *
     * @param initializer The BeanInitializer to use
     * @param define The Define containing the beans to populate
     * @param logger The Logger to use for logging
     * @throws OperationFailedException If the population operation fails
     */
    public void populate(BeanInitializer<?> initializer, Define define, Logger logger)
            throws OperationFailedException {

        PopulateStoreFromDefine<PointsInitialization> populater =
                new PopulateStoreFromDefine<>(define, initializer, logger);
        populater.copyInitialize(PointsFitter.class, pointFitters);
    }
}
