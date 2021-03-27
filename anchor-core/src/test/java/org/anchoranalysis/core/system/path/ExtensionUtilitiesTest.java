package org.anchoranalysis.core.system.path;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
    private static <T> List<Optional<T>> asOptionalAppendEmpty(
            @SuppressWarnings("unchecked") T... element) {
        List<Optional<T>> elements = FunctionalList.mapToList(Arrays.stream(element), Optional::of);
        elements.add(Optional.empty());
        return elements;
    }
}
