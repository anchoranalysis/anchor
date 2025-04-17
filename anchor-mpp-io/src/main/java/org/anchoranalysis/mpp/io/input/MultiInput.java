/*-
 * #%L
 * anchor-mpp-io
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

package org.anchoranalysis.mpp.io.input;

import java.nio.file.Path;
import java.util.Optional;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.stack.input.ProvidesStackInput;
import org.anchoranalysis.image.io.stack.time.ExtractFrameStore;
import org.anchoranalysis.image.io.stack.time.TimeSeries;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.math.histogram.Histogram;
import org.anchoranalysis.mpp.init.MarksInitialization;
import org.anchoranalysis.mpp.mark.MarkCollection;

/**
 * An input to an experiment that combines a particular {@link Stack} with other types of entities.
 *
 * <p>These entities each have a unique name, and must be one of the following types:
 *
 * <ol>
 *   <li>{@link Stack} (in addition to the primary stack that forms the input).
 *   <li>{@link MarkCollection}.
 *   <li>{@link ObjectCollection}.
 *   <li>{@link Dictionary}.
 *   <li>{@link Histogram}.
 *   <li>{@link Path}.
 * </ol>
 *
 * @author Owen Feehan
 */
@Accessors(fluent = true)
public class MultiInput implements ProvidesStackInput, ExportSharedObjects {

    /** The default name for the input image stack. */
    public static final String DEFAULT_IMAGE_INPUT_NAME = "input_image";

    /** The primary stack input with its associated map. */
    private StackWithMap stack;

    /** A map of {@link MarkCollection}s. */
    private OperationMap<MarkCollection> mapMarks = new OperationMap<>();

    /** A map of {@link ObjectCollection}s. */
    private OperationMap<ObjectCollection> mapObjects = new OperationMap<>();

    /** A map of {@link Dictionary}s. */
    private OperationMap<Dictionary> mapDictionary = new OperationMap<>();

    /** A map of {@link Histogram}s. */
    private OperationMap<Histogram> mapHistogram = new OperationMap<>();

    /** A map of {@link Path}s. */
    private OperationMap<Path> mapFilePath = new OperationMap<>();

    /**
     * Creates a new {@link MultiInput} with a default name for the main input object.
     *
     * @param mainInputObject the main input object that provides the stack
     */
    public MultiInput(ProvidesStackInput mainInputObject) {
        this(DEFAULT_IMAGE_INPUT_NAME, mainInputObject);
    }

    /**
     * Creates a new {@link MultiInput} with a specified name for the main input object.
     *
     * @param mainObjectName the name for the main input object
     * @param mainInputObject the main input object that provides the stack
     */
    public MultiInput(String mainObjectName, ProvidesStackInput mainInputObject) {
        this.stack = new StackWithMap(mainObjectName, mainInputObject);
    }

    @Override
    public void addToStoreInferNames(
            NamedProviderStore<TimeSeries> stacks, int seriesIndex, Logger logger)
            throws OperationFailedException {
        stack.addToStore(stacks, seriesIndex, logger);
    }

    @Override
    public void addToStoreWithName(
            String name, NamedProviderStore<TimeSeries> stacks, int seriesIndex, Logger logger)
            throws OperationFailedException {
        throw new OperationFailedException("Not supported");
    }

    @Override
    public void copyTo(SharedObjects target, Logger logger) throws OperationFailedException {

        ImageInitialization image = new ImageInitialization(target);

        stack().addToStore(new ExtractFrameStore(image.stacks()), logger);
        objects().addToStore(image.objects(), logger);
        dictionary().addToStore(image.dictionaries(), logger);
        filePath().addToStore(image.filePaths(), logger);
        histogram().addToStore(image.histograms(), logger);

        marks().addToStore(new MarksInitialization(image).marks(), logger);
    }

    @Override
    public String identifier() {
        return stack.inputName();
    }

    @Override
    public Optional<Path> pathForBinding() {
        return stack.pathForBinding();
    }

    @Override
    public void close(ErrorReporter errorReporter) {
        stack.close(errorReporter);

        // We set all these objects to null so the garbage collector can free up memory
        // This probably isn't necessary, as the MultiInput object should get garbage-collected ASAP
        //   but just in case
        mapMarks = null;
        mapObjects = null;
        mapDictionary = null;
        mapHistogram = null;
        mapFilePath = null;
    }

    /**
     * Gets the map of {@link MarkCollection}s.
     *
     * @return the {@link MultiInputSubMap} of {@link MarkCollection}s
     */
    public MultiInputSubMap<MarkCollection> marks() {
        return mapMarks;
    }

    /**
     * Gets the map of {@link ObjectCollection}s.
     *
     * @return the {@link MultiInputSubMap} of {@link ObjectCollection}s
     */
    public MultiInputSubMap<ObjectCollection> objects() {
        return mapObjects;
    }

    /**
     * Gets the map of {@link Dictionary}s.
     *
     * @return the {@link MultiInputSubMap} of {@link Dictionary}s
     */
    public MultiInputSubMap<Dictionary> dictionary() {
        return mapDictionary;
    }

    /**
     * Gets the map of {@link Histogram}s.
     *
     * @return the {@link MultiInputSubMap} of {@link Histogram}s
     */
    public MultiInputSubMap<Histogram> histogram() {
        return mapHistogram;
    }

    /**
     * Gets the map of {@link Path}s.
     *
     * @return the {@link MultiInputSubMap} of {@link Path}s
     */
    public MultiInputSubMap<Path> filePath() {
        return mapFilePath;
    }

    /**
     * Gets the map of {@link TimeSeries} (stacks).
     *
     * @return the {@link MultiInputSubMap} of {@link TimeSeries}
     */
    public MultiInputSubMap<TimeSeries> stack() {
        return stack;
    }

    /**
     * Gets the name of the main input object.
     *
     * @return the name of the main input object
     */
    public String getMainObjectName() {
        return stack.getMainObjectName();
    }

    @Override
    public int numberFrames() throws OperationFailedException {
        return stack.numFrames();
    }
}
