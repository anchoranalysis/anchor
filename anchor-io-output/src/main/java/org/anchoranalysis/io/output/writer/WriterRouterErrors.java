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
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestDirectoryDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.outputter.Outputter;

/**
 * Write data via {@link ElementWriter}s to the file system, or creates new sub-directories for
 * writng data to.
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
 * <p>The {@link ElementWriterSupplier} interface is used so as to avoid object-creation if an
 * operation isn't actually written.
 *
 * <p>Note that a {@link ElementWriter} may write more than one file for a given element.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class WriterRouterErrors {

    public static final int NUMBER_ELEMENTS_WRITTEN_ERRORED = -1;

    private Writer delegate;
    private ErrorReporter errorReporter;

    /**
     * Maybe creates a subdirectory for writing to.
     *
     * @param outputName the name of the subdirectory
     * @param manifestDescription a manifest-description associated with the subdirectory as a
     *     whole.
     * @param inheritOutputRulesAndRecording if true, the output rules and recording are inherited
     *     from the parent directory. if false, they are not, and all outputs are allowed and are
     *     unrecorded.
     * @return an output-manager for the directory if it is allowed, otherwise {@link
     *     Optional#empty}.
     */
    public Optional<Outputter> createSubdirectory(
            String outputName,
            ManifestDirectoryDescription manifestDescription,
            boolean inheritOutputRulesAndRecording) {
        try {
            return delegate.createSubdirectory(
                            outputName,
                            manifestDescription,
                            Optional.empty(),
                            inheritOutputRulesAndRecording)
                    .map(output -> new Outputter(output, errorReporter));
        } catch (OutputWriteFailedException e) {
            errorReporter.recordError(Outputter.class, e);
            return Optional.empty();
        }
    }

    /**
     * Writes an element using an {@link ElementWriter} to the current directory.
     *
     * @param outputName the name of the subdirectory. This may determine if an output is allowed or
     *     not.
     * @param elementWriter writes the element to the filesystem
     * @param element the element to write
     */
    public <T> void write(
            String outputName, ElementWriterSupplier<T> elementWriter, ElementSupplier<T> element) {
        try {
            delegate.write(outputName, elementWriter, element);
        } catch (OutputWriteFailedException e) {
            errorReporter.recordError(Outputter.class, e);
        }
    }

    /**
     * Writes an indexed-element using an {@link ElementWriter} in the current directory.
     *
     * @param outputNameStyle how to combine a particular output-name with an index
     * @param elementWriter writes the element to the filesystem
     * @param element the element to write
     * @param index the index
     */
    public <T> void writeWithIndex(
            IndexableOutputNameStyle outputNameStyle,
            ElementWriterSupplier<T> elementWriter,
            ElementSupplier<T> element,
            String index) {
        try {
            delegate.writeWithIndex(outputNameStyle, elementWriter, element, index);
        } catch (OutputWriteFailedException e) {
            errorReporter.recordError(Outputter.class, e);
        }
    }

    /**
     * The path to write a particular output to.
     *
     * <p>This is an alternative method to write to the file system rather than using an {@link
     * ElementWriter} and {@link #write(String, ElementWriterSupplier, ElementSupplier)} and {@link
     * #writeWithIndex(IndexableOutputNameStyle, ElementWriterSupplier, ElementSupplier, String)}.
     *
     * @param outputName the output-name. This is the filename without an extension, and may
     *     determine if an output is allowed or not.
     * @param extension the extension
     * @param manifestDescription manifest-description associated with the file if it exists.
     * @return the path to write to, if it is allowed, otherwise {@link Optional#empty}.
     */
    public Optional<Path> createFilenameForWriting(
            String outputName,
            String extension,
            Optional<ManifestDescription> manifestDescription) {
        return delegate.createFilenameForWriting(outputName, extension, manifestDescription);
    }
}
