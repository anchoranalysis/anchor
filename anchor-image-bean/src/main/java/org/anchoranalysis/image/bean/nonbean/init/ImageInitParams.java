/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.bean.nonbean.init;

import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.initializable.params.BeanInitParams;
import org.anchoranalysis.bean.initializable.property.PropertyInitializer;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.identifier.provider.store.StoreSupplier;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.value.KeyValueParams;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.image.bean.provider.ChannelProvider;
import org.anchoranalysis.image.bean.provider.HistogramProvider;
import org.anchoranalysis.image.bean.provider.MaskProvider;
import org.anchoranalysis.image.bean.provider.ObjectCollectionProvider;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.bean.segment.binary.BinarySegmentation;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.size.suggestion.ImageSizeSuggestion;
import org.anchoranalysis.image.core.mask.Mask;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.math.histogram.Histogram;

// A wrapper around SharedObjects which types certain Image entities
public class ImageInitParams implements BeanInitParams {

    @Getter private final SharedObjects sharedObjects;

    @Getter private final Optional<ImageSizeSuggestion> suggestedResize;

    // START: InitParams
    private final KeyValueParamsInitParams soParams;
    private final SharedFeaturesInitParams soFeature;
    // END: InitParams

    // START: Stores
    private final NamedProviderStore<Stack> storeStack;
    private final NamedProviderStore<Histogram> storeHistogram;
    private final NamedProviderStore<ObjectCollection> storeObjects;
    private final NamedProviderStore<Channel> storeChannel;
    private final NamedProviderStore<Mask> storeMask;
    private final NamedProviderStore<BinarySegmentation> storeBinarySegmentation;
    // END: Stores

    private CheckedFunction<StackProvider, Stack, OperationFailedException> stackProviderBridge;

    public ImageInitParams(SharedObjects sharedObjects) {
        this(sharedObjects, Optional.empty());
    }

    public ImageInitParams(
            SharedObjects sharedObjects, Optional<ImageSizeSuggestion> suggestedResize) {
        this.sharedObjects = sharedObjects;
        this.suggestedResize = suggestedResize;
        this.soParams = KeyValueParamsInitParams.create(sharedObjects);
        this.soFeature = SharedFeaturesInitParams.create(sharedObjects);

        storeStack = sharedObjects.getOrCreate(Stack.class);
        storeHistogram = sharedObjects.getOrCreate(Histogram.class);
        storeObjects = sharedObjects.getOrCreate(ObjectCollection.class);
        storeChannel = sharedObjects.getOrCreate(Channel.class);
        storeMask = sharedObjects.getOrCreate(Mask.class);
        storeBinarySegmentation = sharedObjects.getOrCreate(BinarySegmentation.class);
    }

    public NamedProviderStore<Stack> stacks() {
        return storeStack;
    }

    public NamedProviderStore<Histogram> histograms() {
        return storeHistogram;
    }

    public NamedProviderStore<ObjectCollection> objects() {
        return storeObjects;
    }

    public NamedProviderStore<Channel> channels() {
        return storeChannel;
    }

    public NamedProviderStore<Mask> masks() {
        return storeMask;
    }

    public NamedProviderStore<BinarySegmentation> binarySegmentations() {
        return storeBinarySegmentation;
    }

    public KeyValueParamsInitParams params() {
        return soParams;
    }

    public SharedFeaturesInitParams features() {
        return soFeature;
    }

    public void populate(PropertyInitializer<?> propertyInitializer, Define define, Logger logger)
            throws OperationFailedException {

        soFeature.populate(define.getList(FeatureListProvider.class), logger);

        PopulateStoreFromDefine<ImageInitParams> populate =
                new PopulateStoreFromDefine<>(define, propertyInitializer, logger);

        populate.copyInit(BinarySegmentation.class, storeBinarySegmentation);
        populate.copyProvider(MaskProvider.class, storeMask);
        populate.copyProvider(ChannelProvider.class, storeChannel);
        populate.copyProvider(ObjectCollectionProvider.class, storeObjects);
        populate.copyProvider(HistogramProvider.class, storeHistogram);

        stackProviderBridge = populate.copyProvider(StackProvider.class, stacks());
    }

    public void addToStacks(String identifier, Stack inputImage) throws OperationFailedException {
        stacks().add(identifier, () -> inputImage);
    }

    public void addToStacks(String identifier, StackProvider stack)
            throws OperationFailedException {
        StoreAdderHelper.add(identifier, stack, storeStack, stackProviderBridge);
    }

    public void copyStacksFrom(NamedProvider<Stack> source) throws OperationFailedException {

        try {
            for (String id : source.keys()) {
                addToStacks(id, source.getException(id));
            }
        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e.summarize());
        }
    }

    public void copyObjectsFrom(NamedProvider<ObjectCollection> collectionSource)
            throws OperationFailedException {

        for (String id : collectionSource.keys()) {
            addToObjects(
                    id,
                    () -> {
                        try {
                            return collectionSource.getException(id);
                        } catch (NamedProviderGetException e) {
                            throw new OperationFailedException(e.summarize());
                        }
                    });
        }
    }

    public void addToKeyValueParamsCollection(String identifier, KeyValueParams params)
            throws OperationFailedException {
        params().getNamedKeyValueParamsCollection().add(identifier, () -> params);
    }

    public Path getModelDirectory() {
        return sharedObjects.getContext().getModelDirectory();
    }

    private void addToObjects(String identifier, StoreSupplier<ObjectCollection> objects)
            throws OperationFailedException {
        objects().add(identifier, objects);
    }
}
