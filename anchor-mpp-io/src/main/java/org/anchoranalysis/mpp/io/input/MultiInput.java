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
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequenceStore;
import org.anchoranalysis.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.mpp.mark.MarkCollection;

@Accessors(fluent = true)
public class MultiInput implements ProvidesStackInput, InputForMPPBean {

    public static final String DEFAULT_IMAGE_INPUT_NAME = "input_image";

    private StackWithMap stack;

    private OperationMap<MarkCollection> mapMarks = new OperationMap<>();
    private OperationMap<ObjectCollection> mapObjects = new OperationMap<>();
    private OperationMap<KeyValueParams> mapKeyValueParams = new OperationMap<>();
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
            ProgressReporter progressReporter)
            throws OperationFailedException {
        stack.addToStore(stacks, seriesIndex, progressReporter);
    }

    @Override
    public void addToStoreWithName(
            String name,
            NamedProviderStore<TimeSequence> stacks,
            int seriesIndex,
            ProgressReporter progressReporter)
            throws OperationFailedException {
        throw new OperationFailedException("Not supported");
    }

    @Override
    public void addToSharedObjects(MPPInitParams soMPP, ImageInitParams soImage)
            throws OperationFailedException {

        marks().addToStore(soMPP.getMarksCollection());
        stack().addToStore(new WrapStackAsTimeSequenceStore(soImage.stacks()));
        objects().addToStore(soImage.objects());
        keyValueParams().addToStore(soImage.params().getNamedKeyValueParamsCollection());
        filePath().addToStore(soImage.params().getNamedFilePathCollection());
        histogram().addToStore(soImage.histograms());
    }

    @Override
    public String name() {
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
        mapKeyValueParams = null;
        mapHistogram = null;
        mapFilePath = null;
    }

    public MultiInputSubMap<MarkCollection> marks() {
        return mapMarks;
    }

    public MultiInputSubMap<ObjectCollection> objects() {
        return mapObjects;
    }

    public MultiInputSubMap<KeyValueParams> keyValueParams() {
        return mapKeyValueParams;
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
