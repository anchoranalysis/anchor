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

package org.anchoranalysis.io.output.path.prefixer;

import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.io.output.bean.path.prefixer.PathPrefixer;

/**
 * Context parameters provided to a link {@link PathPrefixer}.
 *
 * @author Owen Feehan
 */
public class PathPrefixerContext {

    /** Whether debug-mode is activated. */
    @Getter private final boolean debugMode;

    /** A directory indicating where inputs can be located. */
    @Getter private final Optional<Path> outputDirectory;

    /**
     * Requests outputting with an incrementing number sequence, rather than the usual outputter
     * (normally based upon input filenames).
     */
    @Getter private final boolean outputIncrementingNumberSequence;

    /**
     * Create with default parameters.
     *
     * @throws PathPrefixerException
     */
    public PathPrefixerContext() throws PathPrefixerException {
        this(false, Optional.empty(), false);
    }

    /**
     * Create with specific parameters.
     *
     * @param debugMode whether debug-mode is activated
     * @param outputDirectory a directory indicating where inputs can be located.
     * @param outputIncrementingNumberSequence requests outputting with an incrementing number
     *     sequence, rather than the usual outputter (normally based upon input filenames).
     * @throws PathPrefixerException if the the path in {@code outputDirectory} is relative instead
     *     of absolute
     */
    public PathPrefixerContext(
            boolean debugMode,
            Optional<Path> outputDirectory,
            boolean outputIncrementingNumberSequence)
            throws PathPrefixerException {
        this.debugMode = debugMode;
        this.outputDirectory = outputDirectory;
        this.outputIncrementingNumberSequence = outputIncrementingNumberSequence;
        checkAbsolutePath();
    }

    private void checkAbsolutePath() throws PathPrefixerException {
        if (outputDirectory.isPresent() && !outputDirectory.get().isAbsolute()) {
            throw new PathPrefixerException(
                    String.format(
                            "An non-absolute path was passed to %s of %s",
                            this.getClass().getSimpleName(), outputDirectory.get()));
        }
    }
}
