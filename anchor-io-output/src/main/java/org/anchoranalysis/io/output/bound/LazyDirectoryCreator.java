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

package org.anchoranalysis.io.output.bound;

import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.output.error.OutputManagerAlreadyExistsException;
import org.anchoranalysis.io.output.writer.WriterExecuteBeforeEveryOperation;
import org.apache.commons.io.FileUtils;

/**
 * Creates the output-directory lazily on the first occasion {@link #execute} is called
 *
 * <p>Depending on settings, the initialization routine involves:
 *
 * <ul>
 *   <li>checks if a directory already exists at the path, and throws an errror
 *   <li>deletes existing directory contents
 *   <li>creates the directory and any intermediate paths
 *   <li>first calls an initiation routine on parent initializer
 * </ul>
 */
@RequiredArgsConstructor
class LazyDirectoryCreator implements WriterExecuteBeforeEveryOperation {

    // START REQUIRED ARGUMENTS
    /** The output-directory to be init */
    private final Path outputDirectory;

    /** Whether to delete the existing directory if it exists */
    private final boolean deleteExisting;

    /**
     * A parent whose {@link #execute} is called before our {@link #execute} is called (if empty(),
     * ignored)
     */
    private final Optional<WriterExecuteBeforeEveryOperation> parent;
    // END REQUIRED ARGUMENTS

    /** Has this directory already been initialized/ */
    @Getter private boolean initialized = false;

    @Override
    public synchronized void execute() {
        if (!initialized) {

            parent.ifPresent(WriterExecuteBeforeEveryOperation::execute);

            if (outputDirectory.toFile().exists()) {
                if (deleteExisting) {
                    FileUtils.deleteQuietly(outputDirectory.toFile());
                } else {
                    String line1 = "Output directory already exists.";
                    String line3 = "Consider enabling delExistingFolder=\"true\" in experiment.xml";
                    // Check if it exists already, and refuse to overwrite
                    throw new OutputManagerAlreadyExistsException(
                            String.format(
                                    "%s%nBefore proceeding, please delete: %s%n%s",
                                    line1, outputDirectory, line3));
                }
            }

            // We create any subdirectories as needed
            outputDirectory.toFile().mkdirs();
            initialized = true;
        }
    }
}
