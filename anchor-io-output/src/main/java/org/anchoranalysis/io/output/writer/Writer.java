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
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.folder.FolderWriteWithPath;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Write data via generators to the file system, or creates new sub-directories for writing data to.
 *
 * <p>This class is similar to {@link WriterRouterErrors} but exceptions are thrown rather than
 * reporting errors.
 *
 * <p>These operations occur in association with the currently bound output manager.
 *
 * <p>The {@link GenerateWritableItem} interface is used so as to avoid object-creation if an
 * operation isn't actually written.
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
            ManifestFolderDescription manifestDescription,
            Optional<FolderWriteWithPath> manifestFolder,
            boolean inheritOutputRulesAndRecording)
            throws OutputWriteFailedException;

    /**
     * Writes to a subdirectory using a generator, often producing many elements instead of one.
     *
     * @param outputName the name of the subdirectory. This may determine if an output is allowed
     *     or not.
     * @param generator a generator that writes element(s) into the created subdirectory using
     *     {@code outputName} and a suffix.
     * @return true if the output was allowed, false otherwise
     * @throws OutputWriteFailedException
     */
    boolean writeSubdirectoryWithGenerator(String outputName, GenerateWritableItem<?> generator)
            throws OutputWriteFailedException;

    /**
     * Writes the current element(s) of the generator to the current directory.
     *
     * @param outputNameStyle how to combine a particular output-name with an index
     * @param generator the generator
     * @param index the index
     * @return the number of elements written by the generator, including 0 elements, or -2 if the
     *     output is not allowed.
     * @throws OutputWriteFailedException
     */
    int write(
            IndexableOutputNameStyle outputNameStyle,
            GenerateWritableItem<?> generator,
            String index)
            throws OutputWriteFailedException;

    /**
     * Writes the current element(s) of the generator to the current directory.
     *
     * @param outputName the name of the subdirectory. This may determine if an output is allowed
     *     or not.
     * @param generator a generator that writes element(s) into the created subdirectory using
     *     {@code outputName} and a suffix.
     * @return true if the output was allowed, false otherwise
     * @throws OutputWriteFailedException
     */
    boolean write(String outputName, GenerateWritableItem<?> generator)
            throws OutputWriteFailedException;

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
    Optional<Path> writeGenerateFilename(
            String outputName, String extension, Optional<ManifestDescription> manifestDescription);
}
