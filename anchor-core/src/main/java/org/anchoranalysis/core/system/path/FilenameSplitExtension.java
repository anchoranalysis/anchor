/*-
 * #%L
 * anchor-core
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
package org.anchoranalysis.core.system.path;

import java.io.File;
import java.util.Optional;
import lombok.Value;

/**
 * A file-name with the base (without extension) split from the extension.
 *
 * <p>A period between the base and the extension exists in the filename, but this belongs to
 * neither component.
 *
 * @author Owen Feehan
 */
@Value
public class FilenameSplitExtension {

    /** The part of the filename without an extension. */
    private String baseName;

    /** The extension of the filename. */
    private Optional<String> extension;

    /**
     * Creates from a path without an extension.
     *
     * @param pathWithoutExtension a path without an extension (any directory components are
     *     ignored)
     * @param extension the extension.
     */
    public FilenameSplitExtension(String pathWithoutExtension, Optional<String> extension) {
        this.baseName = new File(pathWithoutExtension).getName();
        this.extension = extension;
    }

    /**
     * Inserts a suffix after the base-name, but before the extension, immutably.
     *
     * @param suffix the suffix to insert
     * @return the full filename with an extension inserted after the basename, but before the
     *     extension
     */
    public String combineWithSuffix(String suffix) {
        String withSuffix = baseName + suffix;
        if (extension.isPresent()) {
            return withSuffix + "." + extension;
        } else {
            return withSuffix;
        }
    }
}
