/*-
 * #%L
 * anchor-io
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

package org.anchoranalysis.io.input;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.io.input.file.NamedFile;

/**
 * One particular input for processing.
 *
 * @author Owen Feehan
 */
public interface InputFromManager {

    /**
     * A unique name associated with the input.
     *
     * @return a string uniquely (in the current dataset) identifying the input in a meaningful way
     */
    String identifier();

    /**
     * Like {@link #identifier} but converts the identifier to a path.
     *
     * @return a path based upon the unique identifier for the input
     */
    default Path identifierAsPath() {
        return Paths.get(identifier());
    }

    /**
     * A path to a file from which this input originated.
     *
     * <p>This path is not guaranteed to be unique for each input i.e. multiple inputs may originate
     * from the same path.
     *
     * @return the primary path associated with the input, if it exists.
     */
    Optional<Path> pathForBinding();

    /**
     * Like {@link #pathForBinding()} but throws an exception if a path isn't present.
     *
     * @return the primary path associated with the input
     * @throws InputReadFailedException if such a path doesn't exist
     */
    default Path pathForBindingRequired() throws InputReadFailedException {
        return pathForBinding()
                .orElseThrow(
                        () ->
                                new InputReadFailedException(
                                        "A binding path is required to be associated with each input for this algorithm, but is not"));
    }

    /**
     * Expresses the input as a file with a name (the unique identifier).
     *
     * @return the file with its associated unique identifier as a name
     * @throws InputReadFailedException if no path is associated with the input
     */
    default NamedFile asFile() throws InputReadFailedException {
        return new NamedFile(identifier(), pathForBindingRequired().toFile());
    }

    /**
     * A path to all files associated with the input, including {@link #pathForBinding()}.
     *
     * <p>Normally an input (e.g. an image) is stored as one file on the file-system, and this is
     * associated with a single file.
     *
     * <p>However, an input (e.g. an image) may originate in several files, and have several
     * associations.
     *
     * <p>Or an input may not originate on the file-system, and thus have no associations.
     *
     * @return a list with all paths associated with the input.
     */
    default List<Path> allAssociatedPaths() {
        Optional<Path> bindingPath = pathForBinding();
        if (bindingPath.isPresent()) {
            return Arrays.asList(bindingPath.get());
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Performs all tidying up, file-closing etc. after we are finished using the {@link
     * InputFromManager}
     */
    default void close(ErrorReporter errorReporter) {}
}
