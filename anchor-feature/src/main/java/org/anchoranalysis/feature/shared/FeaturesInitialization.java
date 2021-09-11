/*-
 * #%L
 * anchor-feature
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

package org.anchoranalysis.feature.shared;

import java.nio.file.Path;
import java.util.List;
import lombok.Getter;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.initializable.params.BeanInitialization;
import org.anchoranalysis.bean.shared.dictionary.DictionaryInitialization;
import org.anchoranalysis.bean.shared.path.FilePathInitialization;
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.log.CommonContext;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.input.FeatureInput;

public class FeaturesInitialization implements BeanInitialization {

    @Getter private DictionaryInitialization dictionary;

    @Getter private FilePathInitialization filePaths;

    @Getter private SharedFeatureMulti sharedFeatures;

    private NamedProviderStore<FeatureList<FeatureInput>> featuresStore;

    private FeaturesInitialization(SharedObjects sharedObjects) {
        this.dictionary = new DictionaryInitialization(sharedObjects);
        this.filePaths = new FilePathInitialization(sharedObjects);

        featuresStore = sharedObjects.getOrCreate(FeatureList.class);

        // We populate our shared features from our storeFeatureList
        sharedFeatures = new SharedFeatureMulti();
        sharedFeatures.addFromProviders(featuresStore);
    }

    public static FeaturesInitialization create(SharedObjects sharedObjects) {
        return new FeaturesInitialization(sharedObjects);
    }

    /**
     * Creates empty params
     *
     * @param logger
     * @return
     */
    public static FeaturesInitialization create(Logger logger, Path modelDirectory) {
        return create(new SharedObjects(new CommonContext(logger, modelDirectory)));
    }

    public NamedProviderStore<FeatureList<FeatureInput>> getFeatureListSet() {
        return featuresStore;
    }

    public void populate(
            List<NamedBean<FeatureListProvider<FeatureInput>>> namedFeatureListCreator,
            Logger logger)
            throws OperationFailedException {

        assert (getFeatureListSet() != null);
        try {
            for (NamedBean<FeatureListProvider<FeatureInput>> namedBean : namedFeatureListCreator) {
                namedBean.getItem().initializeRecursive(this, logger);
                addFeatureList(namedBean);
            }
        } catch (InitializeException e) {
            throw new OperationFailedException(e);
        }
    }

    private void addFeatureList(NamedBean<FeatureListProvider<FeatureInput>> provider)
            throws OperationFailedException {

        try {
            FeatureList<FeatureInput> featureList = provider.getItem().get();
            String name = provider.getName();

            // If there's only one item in the feature list, then we set it as the custom
            //  name of the feature
            if (featureList.size() == 1) {
                featureList.get(0).setCustomName(name);
            }

            featuresStore.add(name, () -> featureList);
            sharedFeatures.addNoDuplicate(featureList);

        } catch (ProvisionFailedException e) {
            throw new OperationFailedException(e);
        }
    }
}
