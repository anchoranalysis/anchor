/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.init;

import org.anchoranalysis.anchor.mpp.bean.points.fitter.PointsFitter;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.bean.nonbean.init.PopulateStoreFromDefine;

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
