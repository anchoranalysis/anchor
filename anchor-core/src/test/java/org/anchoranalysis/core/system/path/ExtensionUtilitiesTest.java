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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.anchoranalysis.core.functional.FunctionalList;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ExtensionUtilities} with three lists of corresponding elements.
 *
 * @author Owen Feehan
 */
class ExtensionUtilitiesTest {

    /** A list of files without extensions. */
    private static List<String> BASE_NAMES =
            Arrays.asList("someFile", "/path/to/someFile", "someFile.one", "someFile", "someFile");

    /** An extension corresponding to each element in {@code BASE_NAMES}. */
    private static List<Optional<String>> EXTENSIONS =
            asOptionalAppendEmpty("png", "tif", "two", "ome.xml");

    /** A list of strings combing each element in {@code BASE_NAMES} and {@code COMBINED}. */
    private static List<String> COMBINED =
            Arrays.asList(
                    "someFile.png",
                    "/path/to/someFile.tif",
                    "someFile.one.two",
                    "someFile.ome.xml",
                    "someFile");

    @Test
    void testExtractExtension() {
        TestUtilities.assertUnary(COMBINED, ExtensionUtilities::extractExtension, EXTENSIONS);
    }

    @Test
    void testRemoveExtension() {
        TestUtilities.assertUnary(COMBINED, ExtensionUtilities::removeExtension, BASE_NAMES);
    }

    @Test
    void testAppendExtension() {
        TestUtilities.assertBinary(
                BASE_NAMES, ExtensionUtilities::appendExtension, EXTENSIONS, COMBINED);
    }

    @Test
    void testSplitFilename() {
        List<FilenameSplitExtension> expectedSplits =
                FunctionalList.mapToList(BASE_NAMES, EXTENSIONS, FilenameSplitExtension::new);

        TestUtilities.assertUnary(COMBINED, ExtensionUtilities::splitFilename, expectedSplits);
    }

    /**
     * Convert every argument to an optional element in a list, and add one extra {@link
     * Optional#empty} element at the end.
     */
    @SuppressWarnings("unchecked")
    private static <T> List<Optional<T>> asOptionalAppendEmpty(T... element) {
        Stream<Optional<T>> stream = Arrays.stream(element).map(Optional::of);
        return Stream.concat(stream, Stream.of((Optional<T>) Optional.empty())).toList();
    }
}
