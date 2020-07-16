/* (C)2020 */
package org.anchoranalysis.feature.shared;

import java.nio.file.Path;
import java.util.List;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.CommonContext;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.list.FeatureListStoreUtilities;

public class SharedFeaturesInitParams implements BeanInitParams {

    private KeyValueParamsInitParams soParams;
    private NamedProviderStore<FeatureList<FeatureInput>> storeFeatureList;
    private SharedFeatureMulti sharedFeatureSet;

    private SharedFeaturesInitParams(SharedObjects so) {
        super();
        this.soParams = KeyValueParamsInitParams.create(so);

        storeFeatureList = so.getOrCreate(FeatureList.class);

        // We populate our shared features from our storeFeatureList
        sharedFeatureSet = new SharedFeatureMulti();
        FeatureListStoreUtilities.addFeatureListToStoreNoDuplicateDirectly(
                storeFeatureList, sharedFeatureSet);
    }

    public static SharedFeaturesInitParams create(SharedObjects sharedObjects) {
        return new SharedFeaturesInitParams(sharedObjects);
    }

    /**
     * Creates empty params
     *
     * @param logger
     * @return
     */
    public static SharedFeaturesInitParams create(Logger logger, Path modelDirectory) {
        return create(new SharedObjects(new CommonContext(logger, modelDirectory)));
    }

    public NamedProviderStore<FeatureList<FeatureInput>> getFeatureListSet() {
        return storeFeatureList;
    }

    public void populate(
            List<NamedBean<FeatureListProvider<FeatureInput>>> namedFeatureListCreator,
            Logger logger)
            throws OperationFailedException {

        assert (getFeatureListSet() != null);
        try {
            for (NamedBean<FeatureListProvider<FeatureInput>> namedBean : namedFeatureListCreator) {
                namedBean.getItem().initRecursive(this, logger);
                addFeatureList(namedBean);
            }
        } catch (InitException e) {
            throw new OperationFailedException(e);
        }
    }

    private void addFeatureList(NamedBean<FeatureListProvider<FeatureInput>> nb)
            throws OperationFailedException {

        try {
            FeatureList<FeatureInput> fl = nb.getItem().create();
            String name = nb.getName();

            // If there's only one item in the feature list, then we set it as the custom
            //  name of teh feature
            if (fl.size() == 1) {
                fl.get(0).setCustomName(name);
            }

            storeFeatureList.add(name, () -> fl);
            sharedFeatureSet.addNoDuplicate(fl);

        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    public KeyValueParamsInitParams getParams() {
        return soParams;
    }

    public SharedFeatureMulti getSharedFeatureSet() {
        return sharedFeatureSet;
    }
}
