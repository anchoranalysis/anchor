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
 */
@Accessors(fluent = true)
public class ImageInitialization implements BeanInitialization {

    /** The name of the dictionary added by {@link #addSharedObjectsDictionary}. */
    public static final String DICTIONARY_IDENTIFIER = "input_dictionary";

    /** Objects shared between different components, a form of shared memory between them. */
    @Getter private final SharedObjects sharedObjects;

    /** A suggested input on how to resize an image, if one is provided. */
    @Getter private final Optional<ImageSizeSuggestion> suggestedSize;

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
     * @param sharedObjects objects shared between different components, a form of shared memory
     *     between them.
     */
    public ImageInitialization(SharedObjects sharedObjects) {
        this(sharedObjects, Optional.empty());
    }

    /**
     * Create with shared-objects and a resizing suggestion.
     *
     * @param sharedObjects objects shared between different components, a form of shared memory
     *     between them.
     * @param suggestedSize a suggested input on how to resize an image, if one is provided.
     */
    public ImageInitialization(
            SharedObjects sharedObjects, Optional<ImageSizeSuggestion> suggestedSize) {
        this.sharedObjects = sharedObjects;
        this.suggestedSize = suggestedSize;
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

    /**
     * Exposes several entities that have natural {@link Stack} representations as a unified {@link
     * NamedProvider}.
     *
     * <p>These are the:
     *
     * <ul>
     *   <li>{@link #stacks()}
     *   <li>{@link #channels()}
     *   <li>{@link #masks()}
     * </ul>
     *
     * <p>If multiple sources have the same identifier, only one identifier (arbitrarily selected)
     * will exist in the unified {@link NamedProvider}.
     *
     * @return a newly created {@link NamedProvider} combining the above entities as {@link Stack}s.
     */
    public NamedProvider<Stack> combinedStacks() {
        return new CombineStackProviders(stacks, channels, masks);
    }

    /**
     * Adds a {@link Dictionary} to the corresponding named-collection of dictionaries.
     *
     * @param identifier the unique name of the dictionary.
     * @param toAdd the dictionary to add.
     * @throws OperationFailedException if the identifier already exists, or otherwise the add
     *     operation fails.
     */
    public void addDictionary(String identifier, Dictionary toAdd) throws OperationFailedException {
        dictionaries().add(identifier, () -> toAdd);
    }

    /**
     * Adds a {@link Stack} to the corresponding named-collection of stacks.
     *
     * @param identifier the unique name of the stack.
     * @param toAdd the stack to add.
     * @throws OperationFailedException if the identifier already exists, or otherwise the add
     *     operation fails.
     */
    public void addStack(String identifier, Stack toAdd) throws OperationFailedException {
        stacks().add(identifier, () -> toAdd);
    }

    /**
     * Adds all the {@link Stack}s available in a {@link NamedProvider} using the corresponding
     * identifiers.
     *
     * @param source the {@link NamedProvider} to add from.
     * @throws OperationFailedException if an identifier already exists, or otherwise the add
     *     operation fails.
     */
    public void addStacksFrom(NamedProvider<Stack> source) throws OperationFailedException {

        try {
            for (String id : source.keys()) {
                addStack(id, source.getException(id));
            }
        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e.summarize());
        }
    }

    /**
     * Adds all the {@link ObjectCollection}s available in a {@link NamedProvider} using the
     * corresponding identifiers.
     *
     * @param source the {@link NamedProvider} to add from.
     * @throws OperationFailedException if an identifier already exists, or otherwise the add
     *     operation fails.
     */
    public void addObjectsFrom(NamedProvider<ObjectCollection> source)
            throws OperationFailedException {

        for (String id : source.keys()) {
            addToObjects(
                    id,
                    () -> {
                        try {
                            return source.getException(id);
                        } catch (NamedProviderGetException e) {
                            throw new OperationFailedException(e.summarize());
                        }
                    });
        }
    }

    /**
     * Adds diverse entities from a {@link Define} into the corresponding name-collections.
     *
     * @param propertyInitializer initializes the properties of objects, where initialization is
     *     required.
     * @param define the {@link Define} from which entities are added.
     * @param logger a logger to report messages or errors.
     * @throws OperationFailedException if the identifier for an entity already exists, or otherwise
     *     the add operation fails.
     */
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

    /**
     * Adds stacks and object-collections from a {@link SharedObjects} using the respective
     * identifiers, and also adds a {@link Dictionary}.
     *
     * <p>The dictionary is assigned the identifier {@value #DICTIONARY_IDENTIFIER}.
     *
     * @param sharedObjects the shared-objects to add entities from, if it exists.
     * @param dictionary the dictionary to add, if it exists.
     * @throws OperationFailedException if the identifier for an entity already exists, or otherwise
     *     the add operation fails.
     */
    public void addSharedObjectsDictionary(
            Optional<SharedObjects> sharedObjects, Optional<Dictionary> dictionary)
            throws OperationFailedException {
        if (sharedObjects.isPresent()) {
            ImageInitialization copyFrom = new ImageInitialization(sharedObjects.get());
            addStacksFrom(copyFrom.stacks());
            addObjectsFrom(copyFrom.objects());
        }

        if (dictionary.isPresent()) {
            addDictionary(DICTIONARY_IDENTIFIER, dictionary.get());
        }
    }

    private void addToObjects(String identifier, StoreSupplier<ObjectCollection> objects)
            throws OperationFailedException {
        objects().add(identifier, objects);
    }
}
