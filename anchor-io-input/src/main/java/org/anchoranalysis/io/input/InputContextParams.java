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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.format.FormatExtensions;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.index.IndexRange;
import org.anchoranalysis.io.input.bean.DebugModeParams;

/**
 * Additional paramaters that offer context for many beans that provide input-functions.
 *
 * @author Owen Feehan
 */
public class InputContextParams {

    /** A list of paths referring to specific inputs */
    @Getter @Setter private Optional<List<Path>> inputPaths;

    /** If defined, a directory which can be used by beans to find input */
    @Getter private Optional<Path> inputDirectory;

    /** A glob that can be used by beans to filter input */
    @Getter @Setter private String inputFilterGlob = "*";

    /** A list of extensions that can be used filter inputs */
    @Getter @Setter private Set<String> inputFilterExtensions = fallBackFilterExtensions();

    /** Parameters for debug-mode (only defined if we are in debug mode) */
    @Getter @Setter private Optional<DebugModeParams> debugModeParams = Optional.empty();

    /**
     * If true, the entire filename or relative path (excluding extension) is used to determine a
     * unique identifier.
     */
    @Getter @Setter private boolean relativeForIdentifier = false;

    /** If true, the order of the inputs are shuffled (randomized). */
    @Getter @Setter private boolean shuffle = false;

    /** If defined, this indicates and specifies only a subset of the naming-elements to use. */
    @Getter @Setter private Optional<IndexRange> identifierSubrange = Optional.empty();

    /**
     * If defined, a directory which can be used by beans to find input.
     *
     * <p>This should always be an absolute path, never a relative one.
     *
     * @param inputDirectory the absolute path of the input-directory.
     * @throws IOException
     */
    public void setInputDirectory(Optional<Path> inputDirectory) throws IOException {
        OptionalUtilities.ifPresent(inputDirectory, InputContextParams::checkAbsolutePath);
        this.inputDirectory = inputDirectory;
    }

    private static void checkAbsolutePath(Path inputDirectory) throws IOException {
        if (!inputDirectory.isAbsolute()) {
            throw new IOException(
                    String.format(
                            "An non-absolute path was passed to setInputDirectory() of %s",
                            inputDirectory));
        }
    }

    /**
     * If no filter extensions are provided from anywhere else, this is a convenient set of
     * defaults.
     */
    private Set<String> fallBackFilterExtensions() {
        return Arrays.stream(FormatExtensions.allImageExtensions()).collect(Collectors.toSet());
    }
}
