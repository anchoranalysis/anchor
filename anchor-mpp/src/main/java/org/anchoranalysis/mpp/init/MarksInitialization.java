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
import lombok.experimental.Accessors;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.initializable.parameters.BeanInitialization;
import org.anchoranalysis.bean.initializable.property.BeanInitializer;
import org.anchoranalysis.bean.shared.dictionary.DictionaryInitialization;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.initialization.FeatureRelatedInitialization;
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
import org.anchoranalysis.mpp.pair.MarkPair;
import org.anchoranalysis.mpp.pair.RandomCollection;

/**
 * Initialization class for marks-related components in the MPP framework.
 *
 * <p>This class manages the initialization of various mark-related stores and provides access to
 * image and points initialization.
 */
@Accessors(fluent = true)
public class MarksInitialization implements BeanInitialization {

    /** The image initialization associated with this marks initialization. */
    @Getter private final ImageInitialization image;

    /** The points initialization associated with this marks initialization. */
    @Getter private final PointsInitialization points;

    /** Store for mark collections. */
    @Getter private final NamedProviderStore<MarkCollection> marks;

    /** Store for mark collection proposers. */
    @Getter private final NamedProviderStore<MarkCollectionProposer> markCollectionProposers;

    /** Store for mark bounds. */
    @Getter private final NamedProviderStore<MarkBounds> markBounds;

    /** Store for mark proposers. */
    @Getter private final NamedProviderStore<MarkProposer> markProposers;

    /** Store for mark merge proposers. */
    @Getter private final NamedProviderStore<MarkMergeProposer> markMergeProposers;

    /** Store for mark split proposers. */
    @Getter private final NamedProviderStore<MarkSplitProposer> markSplitProposers;

    /** Store for random collections of mark pairs. */
    @Getter private final NamedProviderStore<RandomCollection<MarkPair<Mark>>> markPairs;

    /**
     * Constructs a MarksInitialization with the given ImageInitialization.
     *
     * @param image The ImageInitialization to use
     */
    public MarksInitialization(ImageInitialization image) {
        SharedObjects sharedObjects = image.sharedObjects();
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

    /**
     * Gets the FeatureRelatedInitialization associated with this MarksInitialization.
     *
     * @return The FeatureRelatedInitialization
     */
    public FeatureRelatedInitialization feature() {
        return image.featuresInitialization();
    }

    /**
     * Gets the DictionaryInitialization associated with this MarksInitialization.
     *
     * @return The DictionaryInitialization
     */
    public DictionaryInitialization dictionary() {
        return image.dictionaryInitialization();
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

        PopulateStoreFromDefine<MarksInitialization> populater =
                new PopulateStoreFromDefine<>(define, initializer, logger);
        populater.copyWithoutInitialize(MarkBounds.class, markBounds);
        populater.copyInitialize(MarkProposer.class, markProposers);
        populater.copyInitialize(MarkCollectionProposer.class, markCollectionProposers);
        populater.copyInitialize(MarkSplitProposer.class, markSplitProposers);
        populater.copyInitialize(MarkMergeProposer.class, markMergeProposers);
        populater.copyWithoutInitialize(RandomCollection.class, markPairs);

        populater.copyProviderInitialize(MarkCollectionProvider.class, marks());

        image.populate(initializer, define, logger);
        points.populate(initializer, define, logger);
    }
}
