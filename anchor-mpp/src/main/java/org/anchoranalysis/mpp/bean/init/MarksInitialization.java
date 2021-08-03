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

import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.initializable.params.BeanInitialization;
import org.anchoranalysis.bean.initializable.property.PropertyInitializer;
import org.anchoranalysis.bean.shared.dictionary.DictionaryInitialization;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.shared.FeaturesInitialization;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
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

@Accessors(fluent = true)
public class MarksInitialization implements BeanInitialization {

    // START: Initialization
    @Getter private ImageInitialization image;
    @Getter private PointsInitialization points;
    // END: Initialization

    // START: Stores
    @Getter private NamedProviderStore<MarkCollection> marks;
    @Getter private NamedProviderStore<MarkCollectionProposer> markCollectionProposers;
    @Getter private NamedProviderStore<MarkBounds> markBounds;
    @Getter private NamedProviderStore<MarkProposer> markProposers;
    @Getter private NamedProviderStore<MarkMergeProposer> markMergeProposers;
    @Getter private NamedProviderStore<MarkSplitProposer> markSplitProposers;
    @Getter private NamedProviderStore<RandomCollection<IdentifiablePair<Mark>>> markPairs;
    // END: Stores

    public MarksInitialization(ImageInitialization image) {
        SharedObjects sharedObjects = image.getSharedObjects();
        this.image = image;
        this.points = PointsInitialization.create(image, sharedObjects);

        marks = sharedObjects.getOrCreate(MarkCollection.class);
        markCollectionProposers = sharedObjects.getOrCreate(MarkCollectionProposer.class);
        markBounds = sharedObjects.getOrCreate(MarkBounds.class);
        markProposers = sharedObjects.getOrCreate(MarkProposer.class);
        markMergeProposers = sharedObjects.getOrCreate(MarkMergeProposer.class);
        markSplitProposers = sharedObjects.getOrCreate(MarkSplitProposer.class);
        markPairs = sharedObjects.getOrCreate(RandomCollection.class);
    }

    public FeaturesInitialization feature() {
        return image.featuresInitialization();
    }

    public DictionaryInitialization dictionary() {
        return image.dictionaryInitialization();
    }

    public void populate(PropertyInitializer<?> initializer, Define define, Logger logger)
            throws OperationFailedException {

        PopulateStoreFromDefine<MarksInitialization> populater =
                new PopulateStoreFromDefine<>(define, initializer, logger);
        populater.copyWithoutInit(MarkBounds.class, markBounds);
        populater.copyInit(MarkProposer.class, markProposers);
        populater.copyInit(MarkCollectionProposer.class, markCollectionProposers);
        populater.copyInit(MarkSplitProposer.class, markSplitProposers);
        populater.copyInit(MarkMergeProposer.class, markMergeProposers);
        populater.copyWithoutInit(RandomCollection.class, markPairs);

        populater.copyProvider(MarkCollectionProvider.class, marks());

        image.populate(initializer, define, logger);
        points.populate(initializer, define, logger);
    }
}
