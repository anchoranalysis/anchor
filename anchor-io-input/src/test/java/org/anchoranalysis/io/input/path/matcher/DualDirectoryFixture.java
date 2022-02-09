/*-
 * #%L
 * anchor-io-input
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.io.input.path.matcher;

import java.nio.file.Path;
import lombok.AllArgsConstructor;
import org.anchoranalysis.test.TestLoader;

/**
 * Provides access to the {@code FindMatchingFiles} directory in resources.
 *
 * <p>This directory contains two further sdirectories:
 *
 * <ul>
 *   <li>{@value #SUBDIRECTORY_FLAT}: containing four files in the main directory, and no
 *       subdirectories.
 *   <li>{@value #SUBDIRECTORY_NESTED}: containing four files in the main directory, and four more
 *       in nested subdirectories.
 * </ul>
 *
 * @author owen
 */
@AllArgsConstructor
public class DualDirectoryFixture {

    /** Path in src/test/resources to the directory, containing the subdirectories to test. */
    private static final String DIRECTORY_PARENT = "FindMatchingFiles/";

    /** The <i>flat</i> subdirectory as per class description. */
    static final String SUBDIRECTORY_FLAT = "flat01";

    /** The <i>nested</i> subdirectory as per class description. */
    static final String SUBDIRECTORY_NESTED = "nested01";

    /** How to load files from resources. */
    private final TestLoader loader;

    /**
     * Determines a path to one of the two subdirectories.
     *
     * @param nested if true, the nested subdirectory is used, otherwise the flat subdirectory.
     * @return the path to the subdirectory.
     */
    public Path multiplex(boolean nested) {
        String subdirectory = nested ? SUBDIRECTORY_NESTED : SUBDIRECTORY_FLAT;
        return loader.resolveTestPath(DIRECTORY_PARENT + "/" + subdirectory);
    }
}
