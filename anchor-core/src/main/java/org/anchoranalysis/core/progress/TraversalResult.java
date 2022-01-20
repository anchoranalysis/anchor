/*-
 * #%L
 * anchor-core
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
package org.anchoranalysis.core.progress;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * The subdirectories and files found after traversing a directory on the file-system.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public class TraversalResult {

    /**
     * All the directories in the bottom-most depth that was traversed.
     *
     * <p>These directories were not traversed.
     */
    private List<Path> leafDirectories;

    /**
     * All files in any directories that have been traversed.
     *
     * <p>Note that no files from {@code leafDirectories} are present as it was not yet traversed.
     */
    private List<Path> files;

    /**
     * The depth of directories processed, where {@code 1 = root directory} traversed, {@code 2 =
     * root directory + one further level} etc.
     */
    private int depth;

    /**
     * Create with files and a depth, but no leaf directories.
     *
     * @param files all files in any directories that have been traversed.
     * @param depth the depth of directories processed, where {@code 1 = root directory} traversed,
     *     {@code 2 = root directory + one further level} etc.
     */
    public TraversalResult(List<Path> files, int depth) {
        this.leafDirectories = new LinkedList<>();
        this.files = files;
        this.depth = depth;
    }
}
