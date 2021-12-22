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
import lombok.experimental.Accessors;

import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.initializable.parameters.BeanInitialization;
import org.anchoranalysis.bean.initializable.property.BeanInitializer;
import org.anchoranalysis.bean.shared.dictionary.DictionaryBean;
import org.anchoranalysis.bean.shared.dictionary.DictionaryInitialization;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.identifier.provider.store.StoreSupplier;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.feature.bean.FeatureRelatedBean;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.initialization.FeatureRelatedInitialization;
import org.anchoranalysis.image.bean.ImageBean;
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

/**
 * The state used to initialize a {@link ImageBean}.
 * 
 * <p>It contains several {@link NamedProviderStore}s for particular types of entities.
 * 
 * @author Owen
 *
 */
@Accessors(fluent=true)
public class ImageInitialization implements BeanInitialization {

	/** The name of the dictionary added by {@link #addSharedObjectsDictionary}. */
    public static final String DICTIONARY_IDENTIFIER = "input_dictionary";

    /** Objects shared between different components, a form of shared memory between them. */
    @Getter private final SharedObjects sharedObjects;

    /** A suggested input on how to resize an image, if one is provided. */
    @Getter private final Optional<ImageSizeSuggestion> suggestedResize;

    // START: Initialization
    /** The associated initialization for a {@link FeatureRelatedBean}. */
    private final FeatureRelatedInitialization features;
    // END: Initialization

    // START: Stores
    /** A collection of {@link Stack}s, indexed by name. */
    @Getter private final NamedProviderStore<Stack> stacks;
    
    /** A collection of {@link Histogram}s, indexed by name. */
    @Getter private final NamedProviderStore<Histogram> histograms;
    
    /** A collection of {@link ObjectCollection}s, indexed by name. */
    @Getter private final NamedProviderStore<ObjectCollection> objects;
    
    /** A collection of {@link Channel}s, indexed by name. */
    @Getter private final NamedProviderStore<Channel> channels;
    
    /** A collection of {@link Mask}s, indexed by name. */
    @Getter private final NamedProviderStore<Mask> masks;
    
    /** A collection of {@link BinarySegmentation}s, indexed by name. */
    @Getter private final NamedProviderStore<BinarySegmentation> binarySegmentations;
    // END: Stores

    /**
     * Create with shared-objects.
     * 
     * @param sharedObjects objects shared between different components, a form of shared memory between them.
     */
    public ImageInitialization(SharedObjects sharedObjects) {
        this(sharedObjects, Optional.empty());
    }

    /**
     * Create with shared-objects and a resizing suggestion.
     * 
     * @param sharedObjects objects shared between different components, a form of shared memory between them.
     * @param suggestedResize a suggested input on how to resize an image, if one is provided.
     */
    public ImageInitialization(
            SharedObjects sharedObjects, Optional<ImageSizeSuggestion> suggestedResize) {
        this.sharedObjects = sharedObjects;
        this.suggestedResize = suggestedResize;
        this.features = FeatureRelatedInitialization.create(sharedObjects);

        stacks = sharedObjects.getOrCreate(Stack.class);
        histograms = sharedObjects.getOrCreate(Histogram.class);
        objects = sharedObjects.getOrCreate(ObjectCollection.class);
        channels = sharedObjects.getOrCreate(Channel.class);
        masks = sharedObjects.getOrCreate(Mask.class);
        binarySegmentations = sharedObjects.getOrCreate(BinarySegmentation.class);
    }

    /** 
     * Named-store of file-paths.
     * 
     * @return the store.
     */
    public NamedProviderStore<Path> filePaths() {
        return features.getFilePaths().getFilePaths();
    }

    /** 
     * Named-store of {@link Dictionary}s.
     * 
     * @return the store.
     */
    public NamedProviderStore<Dictionary> dictionaries() {
        return dictionaryInitialization().getDictionaries();
    }

    /** 
     * Directory where machine-learning models can be found.
     * 
     * @return the path.
     */
    public Path modelDirectory() {
        return sharedObjects.getContext().getModelDirectory();
    }

    /** 
     * The associated initialization for a {@link DictionaryBean}.
     * 
     * @return the associated initialization.
     */
    public DictionaryInitialization dictionaryInitialization() {
        return features.getDictionary();
    }

    /** 
     * The associated initialization for a {@link FeatureRelatedBean}.
     * 
     * @return the associated initialization.
     */
    public FeatureRelatedInitialization featuresInitialization() {
        return features;
    }

    public void populate(BeanInitializer<?> propertyInitializer, Define define, Logger logger)
            throws OperationFailedException {

        features.populate(define.listFor(FeatureListProvider.class), logger);

        PopulateStoreFromDefine<ImageInitialization> populate =
                new PopulateStoreFromDefine<>(define, propertyInitializer, logger);

        populate.copyInitialize(BinarySegmentation.class, binarySegmentations);
        populate.copyProviderInitialize(MaskProvider.class, masks);
        populate.copyProviderInitialize(ChannelProvider.class, channels);
        populate.copyProviderInitialize(ObjectCollectionProvider.class, objects);
        populate.copyProviderInitialize(HistogramProvider.class, histograms);
        populate.copyProviderInitialize(StackProvider.class, stacks);
    }

    public void addToStacks(String identifier, Stack inputImage) throws OperationFailedException {
        stacks().add(identifier, () -> inputImage);
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

    /**
     * Adds a dictionary to named-collection of dictionaries.
     *
     * @param identifier the unique name of the dictionary
     * @param toAdd the dictionary to add
     * @throws OperationFailedException
     */
    public void addDictionary(String identifier, Dictionary toAdd) throws OperationFailedException {
        dictionaries().add(identifier, () -> toAdd);
    }

    public void addSharedObjectsDictionary(
            Optional<SharedObjects> sharedObjects, Optional<Dictionary> dictionary)
            throws CreateException {
        try {
            if (sharedObjects.isPresent()) {
                ImageInitialization copyFrom = new ImageInitialization(sharedObjects.get());
                copyStacksFrom(copyFrom.stacks());
                copyObjectsFrom(copyFrom.objects());
            }

            if (dictionary.isPresent()) {
                addDictionary(DICTIONARY_IDENTIFIER, dictionary.get());
            }
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }
    
    private void addToObjects(String identifier, StoreSupplier<ObjectCollection> objects)
            throws OperationFailedException {
        objects().add(identifier, objects);
    }
}
