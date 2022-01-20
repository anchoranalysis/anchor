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
