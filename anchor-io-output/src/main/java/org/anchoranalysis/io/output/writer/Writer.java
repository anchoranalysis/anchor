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
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestDirectoryDescription;
import org.anchoranalysis.io.manifest.directory.Subdirectory;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Write data via {@link ElementWriter}s to the file system, or creates new sub-directories for writing data to.
 *
 * <p>This class is similar to {@link WriterRouterErrors} but exceptions are thrown rather than
 * reporting errors.
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
public interface Writer {

    /**
     * Maybe creates a subdirectory for writing to.
     *
     * @param outputName the name of the subdirectory. This may determine if an output is allowed
     *     or not.
     * @param manifestDescription a manifest-description associated with the subdirectory as a
     *     whole.
     * @param manifestFolder a manifest-folder if it exists
     * @param inheritOutputRulesAndRecording if true, the output rules and recording are inherited
     *     from the parent directory. if false, they are not, and all outputs are allowed and are
     *     unrecorded.
     * @return an output-manager for the directory if it is allowed, otherwise {@link
     *     Optional#empty}.
     * @throws OutputWriteFailedException
     */
    Optional<OutputterChecked> createSubdirectory(
            String outputName,
            ManifestDirectoryDescription manifestDescription,
            Optional<Subdirectory> manifestFolder,
            boolean inheritOutputRulesAndRecording)
            throws OutputWriteFailedException;
    
    /**
     * Writes an element using an {@link ElementWriter} to the current directory.
     *
     * @param outputName the name of the subdirectory. This may determine if an output is allowed
     *     or not.
     * @param elementWriter writes the element to the filesystem
     * @param element the element to write
     * @return true if the output was allowed, false otherwise
     * @throws OutputWriteFailedException
     */
    <T> boolean write(String outputName, ElementWriterSupplier<T> elementWriter, ElementSupplier<T> element) throws OutputWriteFailedException;
    
    /**
     * Writes an indexed-element using an {@link ElementWriter} in the current directory.
     *
     * @param outputNameStyle how to combine a particular output-name with an index
     * @param elementWriter writes the element to the filesystem
     * @param element the element to write
     * @param index the index
     * @return the number of elements written by the {@link ElementWriter}, including 0 elements, or -2 if the
     *     output is not allowed.
     * @throws OutputWriteFailedException
     */
    <T> int writeWithIndex(
            IndexableOutputNameStyle outputNameStyle,
            ElementWriterSupplier<T> elementWriter,
            ElementSupplier<T> element,
            String index)
            throws OutputWriteFailedException;

    /**
     * The path to write a particular output to.
     *
     * <p>This is an alternative method to write to the file system rather than using an {@link ElementWriter} and {@link #write(String, ElementWriterSupplier, ElementSupplier)} and {@link #writeWithIndex(IndexableOutputNameStyle, ElementWriterSupplier, ElementSupplier, String)}.
     *
     * @param outputName the output-name. This is the filename without an extension, and may
     *     determine if an output is allowed or not.
     * @param extension the extension
     * @param manifestDescription manifest-description associated with the file if it exists.
     * @return the path to write to, if it is allowed, otherwise {@link Optional#empty}.
     */
    Optional<Path> createFilenameForWriting(
            String outputName, String extension, Optional<ManifestDescription> manifestDescription);
}
