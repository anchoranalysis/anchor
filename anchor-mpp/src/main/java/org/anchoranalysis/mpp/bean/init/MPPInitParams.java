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
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.bean.nonbean.init.PopulateStoreFromDefine;
import org.anchoranalysis.mpp.bean.bound.MarkBounds;
import org.anchoranalysis.mpp.bean.proposer.MarkCollectionProposer;
import org.anchoranalysis.mpp.bean.proposer.MarkMergeProposer;
import org.anchoranalysis.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.mpp.bean.proposer.MarkSplitProposer;
import org.anchoranalysis.mpp.bean.provider.MarkCollectionProvider;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.pair.IdentifiablePair;
import org.anchoranalysis.mpp.pair.RandomCollection;
import org.anchoranalysis.mpp.probmap.ProbMap;

// A wrapper around SharedObjects which types certain MPP entities
public class MPPInitParams implements BeanInitParams {

    // START: InitParams
    private ImageInitParams soImage;
    private PointsInitParams soPoints;
    // END: InitParams

    // START: Stores
    private NamedProviderStore<MarkCollection> storeMarks;
    private NamedProviderStore<MarkCollectionProposer> storeMarksProposer;
    private NamedProviderStore<MarkBounds> storeMarkBounds;
    private NamedProviderStore<MarkProposer> storeMarkProposer;
    private NamedProviderStore<MarkMergeProposer> storeMarkMergeProposer;
    private NamedProviderStore<MarkSplitProposer> storeMarkSplitProposer;
    private NamedProviderStore<ProbMap> storeProbMap;
    private NamedProviderStore<RandomCollection<IdentifiablePair<Mark>>> storePairCollection;
    // END: Stores

    public MPPInitParams(ImageInitParams soImage, SharedObjects so) {
        super();
        this.soImage = soImage;
        this.soPoints = PointsInitParams.create(soImage, so);

        storeMarks = so.getOrCreate(MarkCollection.class);
        storeMarksProposer = so.getOrCreate(MarkCollectionProposer.class);
        storeMarkBounds = so.getOrCreate(MarkBounds.class);
        storeMarkProposer = so.getOrCreate(MarkProposer.class);
        storeMarkMergeProposer = so.getOrCreate(MarkMergeProposer.class);
        storeMarkSplitProposer = so.getOrCreate(MarkSplitProposer.class);
        storeProbMap = so.getOrCreate(ProbMap.class);
        storePairCollection = so.getOrCreate(RandomCollection.class);
    }

    public ImageInitParams getImage() {
        return soImage;
    }

    public SharedFeaturesInitParams getFeature() {
        return soImage.features();
    }

    public PointsInitParams getPoints() {
        return soPoints;
    }

    public KeyValueParamsInitParams getParams() {
        return soImage.params();
    }

    public NamedProviderStore<MarkCollection> getMarksCollection() {
        return storeMarks;
    }

    public NamedProviderStore<MarkCollectionProposer> getMarksProposerSet() {
        return storeMarksProposer;
    }

    public NamedProviderStore<MarkBounds> getMarkBoundsSet() {
        return storeMarkBounds;
    }

    public NamedProviderStore<MarkProposer> getMarkProposerSet() {
        return storeMarkProposer;
    }

    public NamedProviderStore<MarkMergeProposer> getMarkMergeProposerSet() {
        return storeMarkMergeProposer;
    }

    public NamedProviderStore<MarkSplitProposer> getMarkSplitProposerSet() {
        return storeMarkSplitProposer;
    }

    public NamedProviderStore<ProbMap> getProbMapSet() {
        return storeProbMap;
    }

    public NamedProviderStore<RandomCollection<IdentifiablePair<Mark>>> getSimplePairCollection() {
        return storePairCollection;
    }

    /**
     * Backwards compatible for when we only had a single bounds
     *
     * <p>This is stored in all our named-definition files, from now on as "primary"
     *
     * @return
     * @throws NamedProviderGetException
     */
    public MarkBounds getMarkBounds() throws NamedProviderGetException {
        return getMarkBoundsSet().getException("primary");
    }

    public void populate(PropertyInitializer<?> pi, Define define, Logger logger)
            throws OperationFailedException {

        PopulateStoreFromDefine<MPPInitParams> populater =
                new PopulateStoreFromDefine<>(define, pi, logger);
        populater.copyWithoutInit(MarkBounds.class, getMarkBoundsSet());
        populater.copyInit(MarkProposer.class, getMarkProposerSet());
        populater.copyInit(MarkCollectionProposer.class, getMarksProposerSet());
        populater.copyInit(MarkSplitProposer.class, getMarkSplitProposerSet());
        populater.copyInit(MarkMergeProposer.class, getMarkMergeProposerSet());
        populater.copyWithoutInit(RandomCollection.class, getSimplePairCollection());

        populater.copyProvider(MarkCollectionProvider.class, getMarksCollection());

        soImage.populate(pi, define, logger);
        soPoints.populate(pi, define, logger);
    }
}
