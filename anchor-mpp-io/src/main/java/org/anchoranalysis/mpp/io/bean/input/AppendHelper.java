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
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.time.OperationContext;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.reader.StackReader;
import org.anchoranalysis.image.io.histogram.input.HistogramCSVReader;
import org.anchoranalysis.image.io.object.input.ObjectCollectionReader;
import org.anchoranalysis.image.io.stack.input.OpenedImageFile;
import org.anchoranalysis.image.io.stack.time.TimeSeries;
import org.anchoranalysis.io.input.bean.path.DerivePath;
import org.anchoranalysis.mpp.io.input.MultiInput;
import org.anchoranalysis.mpp.io.input.MultiInputSubMap;

/** Helper class for appending various types of data to a {@link MultiInput} object. */
@RequiredArgsConstructor
class AppendHelper {

    /** Helper for deserializing objects. */
    private static final DeserializerHelper<?> DESERIALIZER = new DeserializerHelper<>();

    /** The {@link MultiInput} object to which data will be appended. */
    private final MultiInput input;

    /** Flag indicating whether debug mode is active. */
    private final boolean debugMode;

    /** The context for the current operation. */
    private final OperationContext context;

    /**
     * Functional interface for reading an object from a path.
     *
     * @param <T> the type of object to be read
     */
    @FunctionalInterface
    private interface ReadFromPath<T> {
        /**
         * Reads an object from the given path.
         *
         * @param in the input path
         * @return the read object
         * @throws Exception if an error occurs during reading
         */
        T apply(Path in) throws Exception; // NOSONAR
    }

    /**
     * Appends stacks to the {@link MultiInput}.
     *
     * @param listPaths list of paths to append
     * @param stackReader the stack reader to use
     */
    public void appendStack(List<NamedBean<DerivePath>> listPaths, final StackReader stackReader) {
        append(
                listPaths,
                MultiInput::stack,
                outPath -> {
                    try {
                        return openRaster(outPath, stackReader);
                    } catch (ImageIOException e) {
                        throw new OperationFailedException(e);
                    }
                });
    }

    /**
     * Appends histograms to the {@link MultiInput}.
     *
     * @param listPaths list of paths to append
     */
    public void appendHistogram(List<NamedBean<DerivePath>> listPaths) {
        append(listPaths, MultiInput::histogram, HistogramCSVReader::readHistogramFromFile);
    }

    /**
     * Appends file paths to the {@link MultiInput}.
     *
     * @param listPaths list of paths to append
     */
    public void appendFilePath(List<NamedBean<DerivePath>> listPaths) {
        append(listPaths, MultiInput::filePath, outPath -> outPath);
    }

    /**
     * Appends dictionaries to the {@link MultiInput}.
     *
     * @param listPaths list of paths to append
     */
    public void appendDictionary(List<NamedBean<DerivePath>> listPaths) {
        append(listPaths, MultiInput::dictionary, Dictionary::readFromFile);
    }

    /**
     * Appends marks to the {@link MultiInput}.
     *
     * @param listPaths list of paths to append
     */
    public void appendMarks(List<NamedBean<DerivePath>> listPaths) {
        append(
                listPaths,
                MultiInput::marks,
                serialized -> DESERIALIZER.deserializeMarks(serialized, context));
    }

    /**
     * Appends marks from annotations to the {@link MultiInput}.
     *
     * @param listPaths list of paths to append
     * @param includeAccepted whether to include accepted annotations
     * @param includeRejected whether to include rejected annotations
     */
    public void appendMarksFromAnnotation(
            List<NamedBean<DerivePath>> listPaths,
            boolean includeAccepted,
            boolean includeRejected) {

        append(
                listPaths,
                MultiInput::marks,
                outPath ->
                        DESERIALIZER.deserializeMarksFromAnnotation(
                                outPath, includeAccepted, includeRejected, context));
    }

    /**
     * Appends objects to the {@link MultiInput}.
     *
     * @param listPaths list of paths to append
     */
    public void appendObjects(List<NamedBean<DerivePath>> listPaths) {
        append(
                listPaths,
                MultiInput::objects,
                path -> ObjectCollectionReader.createFromPath(path, context));
    }

    /**
     * Appends new items to a particular {@link MultiInputSubMap} associated with the {@link
     * MultiInput} by transforming paths.
     *
     * @param <T> the type of objects being appended
     * @param list file-generations to read paths from
     * @param extractMap extracts a {@link MultiInputSubMap} from {@code input}
     * @param reader converts from a path to the object of interest
     */
    private <T> void append(
            List<NamedBean<DerivePath>> list,
            Function<MultiInput, MultiInputSubMap<T>> extractMap,
            ReadFromPath<T> reader) {

        for (NamedBean<DerivePath> namedBean : list) {

            MultiInputSubMap<T> map = extractMap.apply(input);

            map.add(
                    namedBean.getName(),
                    () -> readObjectForAppend(input, reader, namedBean, debugMode));
        }
    }

    /**
     * Reads an object for appending to the {@link MultiInput}.
     *
     * @param <T> the type of object being read
     * @param input the {@link MultiInput} object
     * @param reader the reader for the object
     * @param namedBean the named bean containing the path
     * @param debugMode whether debug mode is active
     * @return the read object
     * @throws OperationFailedException if the read operation fails
     */
    private static <T> T readObjectForAppend(
            MultiInput input,
            ReadFromPath<T> reader,
            NamedBean<DerivePath> namedBean,
            boolean debugMode)
            throws OperationFailedException {
        try {
            return reader.apply(
                    namedBean
                            .getValue()
                            .deriveFrom(input::pathForBinding, debugMode)
                            .toAbsolutePath()
                            .normalize());
        } catch (Exception e) {
            throw new OperationFailedException("An error occured appending to the multi-input", e);
        }
    }

    /**
     * Opens a raster image from a path.
     *
     * @param path the path to the image file
     * @param stackReader the stack reader to use
     * @return a {@link TimeSeries} object representing the opened image
     * @throws ImageIOException if an error occurs while opening the image
     */
    private TimeSeries openRaster(Path path, StackReader stackReader) throws ImageIOException {
        try (OpenedImageFile openedFile =
                stackReader.openFile(path, context.getExecutionTimeRecorder())) {
            return openedFile.open(context.getLogger());
        }
    }
}
