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

package org.anchoranalysis.mpp.io.bean.input;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.progress.ProgressIgnore;
import org.anchoranalysis.core.value.KeyValueParams;
import org.anchoranalysis.image.core.stack.TimeSequence;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.StackReader;
import org.anchoranalysis.image.io.histogram.input.HistogramCSVReader;
import org.anchoranalysis.image.io.object.input.ObjectCollectionReader;
import org.anchoranalysis.image.io.stack.input.OpenedRaster;
import org.anchoranalysis.io.input.bean.path.DerivePath;
import org.anchoranalysis.mpp.io.input.MultiInput;
import org.anchoranalysis.mpp.io.input.MultiInputSubMap;

class AppendHelper {

    private static final DeserializerHelper<?> DESERIALIZER = new DeserializerHelper<>();

    /** Reads an object from a path. */
    @FunctionalInterface
    private interface ReadFromPath<T> {
        T apply(Path in) throws Exception; // NOSONAR
    }

    /** It is assumed the input files are single channel images. */
    public static void appendStack(
            List<NamedBean<DerivePath>> listPaths,
            MultiInput input,
            boolean debugMode,
            final StackReader stackReader) {
        append(
                input,
                listPaths,
                MultiInput::stack,
                outPath -> {
                    try {
                        return openRaster(outPath, stackReader);
                    } catch (ImageIOException e) {
                        throw new OperationFailedException(e);
                    }
                },
                debugMode);
    }

    public static void appendHistogram(
            List<NamedBean<DerivePath>> listPaths, MultiInput input, boolean debugMode) {
        append(
                input,
                listPaths,
                MultiInput::histogram,
                HistogramCSVReader::readHistogramFromFile,
                debugMode);
    }

    public static void appendFilePath(
            List<NamedBean<DerivePath>> listPaths, MultiInput input, boolean debugMode) {
        append(input, listPaths, MultiInput::filePath, outPath -> outPath, debugMode);
    }

    public static void appendKeyValueParams(
            List<NamedBean<DerivePath>> listPaths, MultiInput input, boolean debugMode) {

        // Delayed-calculation of the appending path as it can be a bit expensive when multiplied by
        // so many items
        append(
                input,
                listPaths,
                MultiInput::keyValueParams,
                KeyValueParams::readFromFile,
                debugMode);
    }

    public static void appendMarks(
            List<NamedBean<DerivePath>> listPaths, MultiInput input, boolean debugMode) {
        append(input, listPaths, MultiInput::marks, DESERIALIZER::deserializeMarks, debugMode);
    }

    public static void appendMarksFromAnnotation(
            List<NamedBean<DerivePath>> listPaths,
            MultiInput input,
            boolean includeAccepted,
            boolean includeRejected,
            boolean debugMode) {

        append(
                input,
                listPaths,
                MultiInput::marks,
                outPath ->
                        DESERIALIZER.deserializeMarksFromAnnotation(
                                outPath, includeAccepted, includeRejected),
                debugMode);
    }

    public static void appendObjects(
            List<NamedBean<DerivePath>> listPaths, MultiInput input, boolean debugMode) {
        append(
                input,
                listPaths,
                MultiInput::objects,
                ObjectCollectionReader::createFromPath,
                debugMode);
    }

    /**
     * Appends new items to a particular OperationMap associated with the MultiInput by transforming
     * paths
     *
     * @param input the input-object
     * @param list file-generations to read paths from
     * @param extractMap extracts an OperationMap from {@code input}
     * @param reader converts from a path to the object of interest
     * @param debugMode
     */
    private static <T> void append(
            MultiInput input,
            List<NamedBean<DerivePath>> list,
            Function<MultiInput, MultiInputSubMap<T>> extractMap,
            ReadFromPath<T> reader,
            boolean debugMode) {

        for (NamedBean<DerivePath> namedBean : list) {

            MultiInputSubMap<T> map = extractMap.apply(input);

            map.add(
                    namedBean.getName(),
                    () -> readObjectForAppend(input, reader, namedBean, debugMode));
        }
    }

    private static <T> T readObjectForAppend(
            MultiInput input,
            ReadFromPath<T> reader,
            NamedBean<DerivePath> namedBean,
            boolean debugMode)
            throws OperationFailedException {
        try {
            return reader.apply(namedBean.getValue().deriveFrom(input::pathForBinding, debugMode));
        } catch (Exception e) {
            throw new OperationFailedException("An error occured appending to the multi-input", e);
        }
    }

    private static TimeSequence openRaster(Path path, StackReader stackReader)
            throws ImageIOException {
        try (OpenedRaster openedRaster = stackReader.openFile(path)) {
            return openedRaster.open(0, ProgressIgnore.get());
        }
    }
}
