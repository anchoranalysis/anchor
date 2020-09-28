/*-
 * #%L
 * anchor-io-output
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

package org.anchoranalysis.io.output.writer;

import java.nio.file.Path;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.Outputter;

/**
 * Write data via generators to the file system, or creates new sub-directories for writng data to.
 *
 * <p>This class is similar to {@link Writer} but:
 *
 * <ul>
 *   <li>exceptions are suppressed and errors are instead reported.
 *   <li>differences exist around writing sub-folders and manifests
 * </ul>
 *
 * <p>These operations occur in association with the currently bound output manager.
 *
 * <p>The {@link GenerateWritableItem} interface is used so as to avoid object-creation if an
 * operation isn't actually written.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class WriterRouterErrors {

    public static final int NUMBER_ELEMENTS_WRITTEN_ERRORED = -1;

    private Writer delegate;
    private ErrorReporter errorReporter;

    /**
     * Maybe creates a sub-directory for writing to.
     *
     * @param outputName the name of the sub-directory
     * @param manifestDescription a manifest-description associated with the sub-directory as a
     *     whole.
     * @return an output-manager for the directory if it is allowed, otherwise {@link
     *     Optional#empty}.
     */
    public Optional<Outputter> createSubdirectory(
            String outputName, ManifestFolderDescription manifestDescription) {
        try {
            return delegate.createSubdirectory(outputName, manifestDescription, Optional.empty())
                    .map(output -> new Outputter(output, errorReporter));
        } catch (OutputWriteFailedException e) {
            errorReporter.recordError(Outputter.class, e);
            return Optional.empty();
        }
    }

    /**
     * Writes to a sub-directory using a generator, often producing many elements instead of one.
     *
     * @param outputName the name of the sub-directory. This may determine if an output is allowed
     *     or not.
     * @param generator a generator that writes its current element(s) into the created
     *     sub-directory using {@code outputName} and a suffix.
     */
    public void writeSubfolderWithGenerator(String outputName, GenerateWritableItem<?> generator) {
        try {
            delegate.writeSubdirectoryWithGenerator(outputName, generator);
        } catch (OutputWriteFailedException e) {
            errorReporter.recordError(Outputter.class, e);
        }
    }

    /**
     * Writes the current element(s) of the generator to the current directory.
     *
     * @param outputNameStyle how to combine a particular output-name with an index
     * @param generator the generator
     * @param index the index
     * @return the number of elements written by the generator, including 0 elements, or -1 if an
     *     error occurred, or -2 if the output was not allowed.
     */
    public int write(
            IndexableOutputNameStyle outputNameStyle,
            GenerateWritableItem<?> generator,
            String index) {
        try {
            return delegate.write(outputNameStyle, generator, index);
        } catch (OutputWriteFailedException e) {
            errorReporter.recordError(Outputter.class, e);
            return NUMBER_ELEMENTS_WRITTEN_ERRORED;
        }
    }

    /**
     * Writes the current element(s) of the generator to the current directory.
     *
     * @param outputName the name of the sub-directory. This may determine if an output is allowed
     *     or not.
     * @param generator a generator that writes element(s) into the created sub-directory using
     *     {@code outputName} and a suffix.
     */
    public void write(String outputName, GenerateWritableItem<?> generator) {
        try {
            delegate.write(outputName, generator);
        } catch (OutputWriteFailedException e) {
            errorReporter.recordError(Outputter.class, e);
        }
    }

    /**
     * The path to write a particular output to.
     *
     * <p>This is an alternative means to using a generator (i.e. {@link GenerateWritableItem}) to
     * output data.
     *
     * @param outputName the output-name. This is the filename without an extension, and may
     *     determine if an output is allowed or not.
     * @param extension the extension
     * @param manifestDescription manifest-description associated with the file if it exists.
     * @return the path to write to, if it is allowed, otherwise {@link Optional#empty}.
     */
    public Optional<Path> writeGenerateFilename(
            String outputName,
            String extension,
            Optional<ManifestDescription> manifestDescription) {
        return delegate.writeGenerateFilename(outputName, extension, manifestDescription);
    }
}
