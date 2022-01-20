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

package org.anchoranalysis.io.input.file;

import java.io.File;
import java.nio.file.Path;
import java.util.function.BiFunction;
import lombok.Value;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;

/**
 * A file with an associated unique name.
 *
 * <p>This is an <i>immutable</i> class.
 *
 * <p>The name is intended to be a compact unique identifier for a file (in a particular context).
 *
 * @author Owen Feehan
 */
@Value
public class NamedFile {

    /** Unique identifier associated with the file. */
    private String identifier;

    /** The file. */
    private File file;

    /**
     * Create with an identifier and a file.
     *
     * @param identifier unique identifier associated with the file.
     * @param file the file.
     */
    public NamedFile(String identifier, File file) {
        this.identifier = identifier;
        this.file = file;
        if (identifier.startsWith(" ") || identifier.endsWith(" ")) {
            throw new AnchorFriendlyRuntimeException(
                    "An identifier is not permitted to begin or end with whitespace: "
                            + identifier);
        }
    }

    /**
     * The path of {@link #getFile}.
     *
     * @return the path
     */
    public Path getPath() {
        return file.toPath();
    }

    /**
     * Immutably renames the file, by assigning it a new identifier.
     *
     * @param function generates a new identifier from the existing identifier and file
     * @return a newly created file with the assigned identifier, but existing file.
     */
    public NamedFile mapIdentifier(BiFunction<String, File, String> function) {
        return new NamedFile(function.apply(identifier, file), file);
    }

    /**
     * Like {@link File#toPath}.
     *
     * @return the path associated with the file.
     */
    public Path toPath() {
        return file.toPath();
    }
}
