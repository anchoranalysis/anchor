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
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.TimeSequence;
import org.anchoranalysis.image.core.stack.time.WrapStackAsTimeSequenceStore;
import org.anchoranalysis.image.io.stack.input.ProvidesStackInput;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.math.histogram.Histogram;
import org.anchoranalysis.mpp.init.MarksInitialization;
import org.anchoranalysis.mpp.mark.MarkCollection;

/**
 * An input to an experiment that combines a particular {@link Stack} with other types of entities.
 *
 * <p>Theses entities each have a unique name, and must be one of the following types:
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

    public static final String DEFAULT_IMAGE_INPUT_NAME = "input_image";

    private StackWithMap stack;

    private OperationMap<MarkCollection> mapMarks = new OperationMap<>();
    private OperationMap<ObjectCollection> mapObjects = new OperationMap<>();
    private OperationMap<Dictionary> mapDictionary = new OperationMap<>();
    private OperationMap<Histogram> mapHistogram = new OperationMap<>();
    private OperationMap<Path> mapFilePath = new OperationMap<>();

    public MultiInput(ProvidesStackInput mainInputObject) {
        this(DEFAULT_IMAGE_INPUT_NAME, mainInputObject);
    }

    public MultiInput(String mainObjectName, ProvidesStackInput mainInputObject) {
        this.stack = new StackWithMap(mainObjectName, mainInputObject);
    }

    @Override
    public void addToStoreInferNames(
            NamedProviderStore<TimeSequence> stacks,
            int seriesIndex,
            Progress progress,
            Logger logger)
            throws OperationFailedException {
        stack.addToStore(stacks, seriesIndex, progress, logger);
    }

    @Override
    public void addToStoreWithName(
            String name,
            NamedProviderStore<TimeSequence> stacks,
            int seriesIndex,
            Progress progress,
            Logger logger)
            throws OperationFailedException {
        throw new OperationFailedException("Not supported");
    }

    @Override
    public void copyTo(SharedObjects target, Logger logger) throws OperationFailedException {

        ImageInitialization image = new ImageInitialization(target);

        stack().addToStore(new WrapStackAsTimeSequenceStore(image.stacks()), logger);
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

    public MultiInputSubMap<MarkCollection> marks() {
        return mapMarks;
    }

    public MultiInputSubMap<ObjectCollection> objects() {
        return mapObjects;
    }

    public MultiInputSubMap<Dictionary> dictionary() {
        return mapDictionary;
    }

    public MultiInputSubMap<Histogram> histogram() {
        return mapHistogram;
    }

    public MultiInputSubMap<Path> filePath() {
        return mapFilePath;
    }

    public MultiInputSubMap<TimeSequence> stack() {
        return stack;
    }

    public String getMainObjectName() {
        return stack.getMainObjectName();
    }

    @Override
    public int numberFrames() throws OperationFailedException {
        return stack.numFrames();
    }
}
