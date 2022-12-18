/*-
 * #%L
 * anchor-io-input
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.io.input.file;

import java.nio.file.Path;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.core.index.range.IndexRangeNegative;
import org.anchoranalysis.core.log.Logger;

/**
 * Provides useful additional objects when assigning a name to a file.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public class FileNamerContext {

    private static final String DEFAULT_ELSE_NAME = "unknownName";

    /**
     * A directory associated with the inputs, which if defined, is guaranteed to be a parent of
     * them all.
     */
    private Optional<Path> inputDirectory;

    /**
     * If true, the namer should prefer to derive file-names relative to the directory, rather than
     * only the varying elements in the file-names.
     */
    private boolean relativeToDirectory;

    /** A fallback name, if a failure occurs when naming. */
    private String elseName;

    /** If defined, this indicates and specifies only a subset of the naming-elements to use. */
    private Optional<IndexRangeNegative> nameSubrange;

    /** Logs information messages. */
    private Logger logger;

    /**
     * Creates with a logger, and otherwise uses sensible defaults.
     *
     * @param logger the logger.
     */
    public FileNamerContext(Logger logger) {
        this(Optional.empty(), false, Optional.empty(), logger);
    }

    /**
     * Creates with specific parameters.
     *
     * @param inputDirectory a directory associated with the inputs, which if defined, is guaranteed
     *     to be a parent of them all.
     * @param relativeToDirectory if true, the namer should prefer to derive file-names relative to
     *     the directory, rather than only the varying elements in the file-names.
     * @param nameSubrange if defined, this indicates and specifies only a subset of the
     *     naming-elements to use.
     * @param logger the logger.
     */
    public FileNamerContext(
            Optional<Path> inputDirectory,
            boolean relativeToDirectory,
            Optional<IndexRangeNegative> nameSubrange,
            Logger logger) {
        this(inputDirectory, relativeToDirectory, DEFAULT_ELSE_NAME, nameSubrange, logger);
    }

    /**
     * Creates with a fallback-name and a logger, but otherwise using sensible defaults.
     *
     * @param elseName a fallback-name, if a failure occurs when naming.
     * @param logger the logger.
     */
    public FileNamerContext(String elseName, Logger logger) {
        this(Optional.empty(), false, elseName, Optional.empty(), logger);
    }
}
